package sysprogxe;
import java.io.File;
import java.util.Scanner;
/**** @author nadazayed */
public class SysProgXE 
{
    public static void main(String[] args) throws Exception 
    {
        String [][] reg = new String[6][2];
        reg[0]  = new String[] {"A", "0"};
        reg[1]  = new String[] {"X", "1"};
        reg[2]  = new String[] {"B", "4"};
        reg[3]  = new String[] {"S", "5"};
        reg[4]  = new String[] {"T", "6"};
        reg[5]  = new String[] {"F", "7"};
        
        String [] tag=new String  [49];  //ex loop
        String [] ins=new String  [49];  //ex LDA
        String [] add=new String  [49];  //ex Alpha
        
        //----- File scan -----//
        File file = new File("inSICXE.txt");
        String word=null;
        Scanner sc = new Scanner(file); 
        int i=0,cnt=-1;
        
        //----- Filling tag, ins, add -----//
         while (sc.hasNextLine())
         {
             word=sc.nextLine();
            String[] temp = word.split("	"); //split by tab
            
            if (temp[0].matches(".*[A-Z].*")) //lw bad2 b klma
            {
                if(temp[0].matches("END"))
            {
                tag[i]=temp[0];
                ins[i]=temp[1];
                add[i]=";";
            }
            else
            {
                tag[i]=temp[0];
                ins[i]=temp[1];
                add[i]=temp[2];
                cnt++;
            }
            i++;
            }
            
            else 
            {
                if (temp[1].matches("RSUB"))
            {
                tag[i]=";";
                ins[i]=temp[1];
                add[i]=";";
            }
                else
            {
                tag[i]=";";
                ins[i]=temp[1];
                add[i]=temp[2];
            }
                i++;
            }
            
         }
         
         
         //----- filling loc-----//
         
         String [] loc=new String  [49];  //ex 1003
         loc[0] =("0000"+add[0]).substring(add[0].length()); //start
         loc[1] =("0000"+add[0]).substring(add[0].length()); //1st ins
         
         String format="", hex="", base="";
         int dec=0,chars;
         
         for (i=1;i<ins.length-1;i++)
         {
             if (ins[i].startsWith("+")) //format 4
             {
                 dec=Integer.parseInt(loc[i],16)+4;
                 loc[i+1]=("0000"+Integer.toHexString(dec)).substring(Integer.toHexString(dec).length());
             }
             
             else if (ins[i].matches("BYTE"))
             {
                 chars=add[i].length()-3; //length el string mn8er X'' aw C''
                 
                if (add[i].startsWith("X"))
                {
                    dec=chars/2;
                    loc[i+1]=("0000"+Integer.toHexString(dec + Integer.parseInt(loc[i],16))).substring(Integer.toHexString(dec + Integer.parseInt(loc[i],16)).length()); //1st ins
                }
                else //C' '
                {
                    loc[i+1]=("0000"+Integer.toHexString(chars + Integer.parseInt(loc[i],16))).substring(Integer.toHexString(chars + Integer.parseInt(loc[i],16)).length()); //1st ins
                    
                }
             }
             
             else if (ins[i].matches("RESB"))
             {
                dec=Integer.parseInt(add[i])*1;
                loc[i+1]=("0000"+Integer.toHexString(dec + Integer.parseInt(loc[i],16))).substring(Integer.toHexString(dec + Integer.parseInt(loc[i],16)).length());
             }
             
             else if (ins[i].matches("WORD"))
             {
                 dec=Integer.parseInt(loc[i],16)+3;
                 loc[i+1]=("0000"+Integer.toHexString(dec)).substring(Integer.toHexString(dec).length());
             }
             
             else if (ins[i].matches("RESW"))
             {
                dec=Integer.parseInt(add[i])*3;
                loc[i+1]=("0000"+Integer.toHexString(dec + Integer.parseInt(loc[i],16))).substring(Integer.toHexString(dec + Integer.parseInt(loc[i],16)).length());
             }
             
             else if (ins[i].matches("BASE")||ins[i].matches("EQU")) //msh hnzwd 7aga or EQU
             {
                 loc[i+1]=loc[i];
             }
             
             else //ins 3ady
             {
                 format=converter.getFORMAT(ins[i]);
                 if (Integer.parseInt(format)==2) //format 2
                 {
                     dec=Integer.parseInt(loc[i],16)+2;
                 }
                 
                 else //format 3 
                     dec=Integer.parseInt(loc[i],16)+3;
                 
                loc[i+1]=("0000"+Integer.toHexString(dec)).substring(Integer.toHexString(dec).length());
             }
             
         }
         
         
         
         //----- filling symbol table -----//
        String [] symTab1=new String [cnt]; //symbol
        String [] symTab2=new String [cnt]; //location
        int j=0;
        for (i=1;i<ins.length-1;i++)
        {
            if (!tag[i].matches(";"))
            {
                if (ins[i].matches("EQU"))
                {
                    System.out.println("here EQU"+tag[i]);
                    symTab1[j]=tag[i];
                        if (add[i].equals("*"))
                        {
                            System.out.println("here *"+loc[i]);
                             symTab2[j]=loc[i];
                             System.out.println(symTab2[j]);
                        }
                        else
                        {
                            if (add[i].contains("-"))
                            {
                                System.out.println("here -"+tag[i]);
                                String [] c=add[i].split("-");
                                String a=c[0],b=c[1];
                                System.out.println("here a "+a+" b "+b);
                                boolean f=false,ff=false;
                                for (int t=0;t<symTab1.length;t++)
                                {
                                    if (symTab1[t].equals(a))
                                    {
                                        a=symTab2[t];
                                        System.out.println(add[i]+"\t"+a+"\t"+symTab2[t]);
                                        f=true;
                                    }
                                        
                                     if (symTab1[t].equals(b))
                                    {
                                        b=symTab2[t];
                                        System.out.println(add[i]+"\t" +b+"\t"+symTab2[t]);
                                        ff=true;
                                    }
                                     if(f==true && ff==true)
                                        break;
                                        
                                }
                                symTab2[j]=Integer.toHexString(Integer.parseInt(a,16)-Integer.parseInt(b,16));
                            }
                        }
                    j++;
                }
                else
                {
                    symTab1[j]=tag[i];
                    symTab2[j]=loc[i];
                    j++;
                }
                
            }
        }
         
        for (i=0;i<ins.length;i++) //get base value
        {
            if (ins[i].matches("BASE"))
            {
                for (j=0;j<symTab1.length;j++)
                {
                    if(add[i].matches(symTab1[j]))
                    {
                        base=symTab2[j];
                    }
                }
            }
        }
         
         //---------------------------Pass II--------------------------//
         
         String [] objectcode =new String  [49]; //object code array
//         
         String byte_chars="", opcode_hex="",opcode_bin="", bin1="", bin2="",  disp="", temp="", TA="";
         char [] c2;
         String [] reg12;
         objectcode[0]=";";
         objectcode[objectcode.length-1]=";";
      
         for (i=1;i<objectcode.length-1;i++)
         {
             format=converter.getFORMAT(ins[i]);
             opcode_hex=converter.getOp(ins[i]);
             
             if (ins[i].matches("RESW")|| ins[i].matches("RESB")|| ins[i].matches("BASE")|| ins[i].matches("RSUB")|| ins[i].matches("EQU"))
             {
                 objectcode[i]=";";
             }// no obj
             
             else if (ins[i].matches("WORD"))
             {
                 objectcode[i]=("000000"+Integer.toHexString(Integer.parseInt(add[i],16))).substring(Integer.toHexString(Integer.parseInt(add[i],16)).length());
             }// word
             
             else if (ins[i].matches("BYTE"))
             {
                 if (add[i].contains("X")) //X' '
                {
                    objectcode[i]=add[i].substring(2, add[i].length()-1);
                }
                 
                 else // C' ' > ASCII CODE
                {
                    char [] c=add[i].substring(2,add[i].length()-1).toCharArray();
                    for (int t=0;t<c.length;t++)
                    {
                        byte_chars=byte_chars+(int)c[t];
                        
                    }
                    objectcode[i]=byte_chars;
                }
             }// byte
             
             else if (format.matches("2"))
             {
                 String reg1="", reg2="";
                 
                 reg12=add[i].split(",");
                 if (reg12.length==2) //fi coma
                 {
                     reg1=reg12[0];
                     reg2=reg12[1];
                 }
                 
                 for (j=0; j<reg.length; j++)
                {
                    if(add[i].matches(reg[j][0])) // 1 reg
                    {
                        disp=("00"+reg[j][1]).substring(reg[j][1].length());
                        break;
                    }
                    else
                    {
                        if (reg1.matches(reg[j][0]))
                            reg1=reg[j][1];
                        if (reg2.matches(reg[j][0]))
                            reg2=reg[j][1];
                        //disp=("00"+reg1).substring(reg1.length())+("00"+reg2).substring(reg2.length());
                        disp=reg1+reg2;
                    }
                }
                 
                 String full=opcode_hex+disp;
                 objectcode[i]=full;
             }// format 2
             
             else if(format.matches("3")) //format 3
             {
                 int flag1=-1, flag2=-1, flag3=-1;
                 String nixbpe="";
                 ////--------------opCode-----------6Bits-------------////
                 c2=opcode_hex.toCharArray();
                 bin1=("0000"+Integer.toBinaryString(Integer.parseInt(c2[0]+"",16))).substring(Integer.toBinaryString(Integer.parseInt(c2[0]+"",16)).length());
                 bin2=("0000"+Integer.toBinaryString(Integer.parseInt(c2[1]+"",16))).substring(Integer.toBinaryString(Integer.parseInt(c2[1]+"",16)).length());
                 bin2=bin2.substring(0, bin2.length()-2);
                 opcode_bin=bin1+bin2;
                 
                 ///-------------------------------------------------------------------//
                 
                 ////--------------Disp-----------3bits------Hex-----/////
                 temp=add[i];
                 for (j=0;j<symTab1.length;j++) // getting TA
                     {
                         if (add[i].startsWith("@")||add[i].startsWith("#")) 
                             temp=add[i].substring(1,add[i].length()); //remove @ sign
                         
                         if (add[i].contains(","))
                         {
                             temp=add[i].substring(0,add[i].length()-2); //remove ,X
                             flag3=1;
                         }
                         
                         if (temp.matches(symTab1[j]))
                         {
                             TA=symTab2[j];
                             flag2=1; //mwgooda
                             break;
                         }
                     }
                 
                 
                 if (add[i].startsWith("#")&&flag2==-1)
                 {
                     if (flag2==-1)
                        disp=("000"+Integer.toHexString(Integer.parseInt(temp,16))).substring(Integer.toHexString(Integer.parseInt(temp,16)).length());    
                 }
                 else //@ or direct
                        {
                         disp=("000"+Integer.toHexString(Integer.parseInt(TA,16)-Integer.parseInt(loc[i+1],16))).substring(Integer.toHexString(Integer.parseInt(TA,16)-Integer.parseInt(loc[i+1],16)).length());
                         
                         if (Integer.parseInt(disp,16)>2047) //Base rel
                            {
                         flag1=1;
                         disp=("000"+Integer.toHexString(Integer.parseInt(TA,16)-Integer.parseInt(base,16))).substring(Integer.toHexString(Integer.parseInt(TA,16)-Integer.parseInt(base,16)).length());
                         }
                        }
                 //-----------------------------------------------------------//
                 
                 
                 String full="";
                 if (add[i].startsWith("@")) // indirect 
                 {      
                        nixbpe="100010"; // pc rel
                     if (flag3==1) // indexed pc
                        nixbpe="101010";
                     if (flag1==1) // base rel
                        nixbpe="100100";
                     if (flag1==1&&flag3==1)
                         nixbpe="101100"; 
                 }
                 
                 else if (add[i].startsWith("#")) //immediate
                 {
                     nixbpe="010000";
                     if (flag2==1)
                         nixbpe="010010";
                 }
                 
                 else //ay ins 3ady orrr indexed
                 {
                        nixbpe="110010"; //pc rel
                     if(flag3==1) // pc indexed
                         nixbpe="111010";
                     
                     if (flag1==1) //base rel
                         nixbpe="110100";
                     if(flag1==1&&flag3==1)// base indexed
                         nixbpe="111100";
                     
                     
                 }
                 
                 
                     full=Integer.toHexString(Integer.parseInt((opcode_bin+nixbpe),2))+disp;
                     full=("000000"+full).substring(full.length());
                     objectcode[i]=full;
             }// format 3
             
             else if (ins[i].startsWith("+")) //format 4
             {
                 String nixbpe,full;
                 int flag=0;
                 opcode_hex=converter.getOp(ins[i].substring(1,ins[i].length()));
                 c2=opcode_hex.toCharArray();
                 bin1=("0000"+Integer.toBinaryString(Integer.parseInt(c2[0]+"",16))).substring(Integer.toBinaryString(Integer.parseInt(c2[0]+"",16)).length());
                 bin2=("0000"+Integer.toBinaryString(Integer.parseInt(c2[1]+"",16))).substring(Integer.toBinaryString(Integer.parseInt(c2[1]+"",16)).length());
                 bin2=bin2.substring(0, bin2.length()-2);
                 opcode_bin=bin1+bin2; //pincode
                 
                 
                 temp=add[i];
                 if (temp.startsWith("#"))
                 {
                     temp=add[i].substring(1,add[i].length());
                     nixbpe="010001";
                 }
                 else if (temp.startsWith("@"))
                 {
                     temp=add[i].substring(1,add[i].length());
                     nixbpe="100001";
                 }
                     
                 else
                     nixbpe="110001";
                 
                 
                 for (j=0;j<symTab1.length;j++) // getting TA for @LENGTH .. #LENGTH .. LENGTH
                 {
                     if (temp.matches(symTab1[j]))
                     {
                         disp=("00000"+symTab2[j]).substring(symTab2[j].length());
                         flag=1;
                     }
                 }
                 
                 if (flag==0) //#50
                     disp=("00000"+temp).substring(temp.length());
                 
                 full=Integer.toHexString(Integer.parseInt((opcode_bin+nixbpe),2))+disp;
                 objectcode[i]=full;
             }// format 4
             
         }//for
//            
         System.out.println("Location"+"\t"+"Tag"+"\t \t"+"Instruction"+"\t"+"Address");
         System.out.println("------------------------------------------------------------------------");
         for (i=0;i<46;i++)
         {
             System.out.println(loc[i]+"\t \t"+tag[i]+"\t \t"+ins[i]+"\t \t"+add[i]+"\t \t "+objectcode[i]);
         }
         
         System.out.println("\n\nTag"+"\t\t"+"Address");
         System.out.println("-----------------------");
         for (j=0;j<symTab1.length;j++)
         {
             System.out.println(symTab1[j]+"\t \t"+symTab2[j]);
         }
//         
         //---------------------------HTE--------------------------//
         //H:-
         String Hname=("      "+tag[0]).substring(tag[0].length());
         String Hstart=("000000"+add[0]).substring(add[0].length());
         String Hlength=("000000"+loc[loc.length-1]).substring(loc[loc.length-1].length());
         
         System.out.println("\n \n"+"----------------- HTE Record -----------------");
         System.out.println("H^Name:"+Hname+"^Start:"+Hstart+"^Length:"+Hlength);
         
         //T:-
         String Tstart="",start="", Tlength="",len="", T="";
         String [] splitting;
         for (i=1;i<objectcode.length;i++)
         {
             if (!objectcode[i].matches(";"))
             {
                 if(objectcode[i-1].matches(";"))
                 {
                     Tstart="";
                     T="";
                 }
                 Tstart=Tstart+"m"+loc[i];
                 T=T+"^"+objectcode[i];
             }
             else
             {
                 if (!objectcode[i-1].matches(";"))
                 {
                     splitting=Tstart.split("m");
                     start=("000000"+splitting[1]).substring(splitting[1].length());
                     len=Integer.toHexString(Integer.parseInt((splitting[splitting.length-1]),16)-Integer.parseInt(start,16));
                     len=("00"+len).substring(len.length());
                     T=T.substring(1,T.length());
                     System.out.println("T^"+start+"^"+len+"^"+T);
                 }
             }
         }
         
         for (i=0;i<ins.length;i++)
         {
             if(ins[i].startsWith("+"))
             {
                 len=Integer.toHexString(Integer.parseInt(loc[i],16)+1);
                 len=("000000"+len).substring(len.length());
                 System.out.println("M^"+len+"^05");
             }
         }
         
         System.out.println("E^"+Hstart);
         
         
         
    }//main
    
}//class
