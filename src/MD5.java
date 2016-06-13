import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;

public class MD5{
    /*
    *四个链接变量
    *   01234567
        89abcdef
        fedcba98
        76543210
        小端序
    */
    public static final int A=0x67452301;
    public static final int B=0xefcdab89;
    public static final int C=0x98badcfe;
    public static final int D=0x10325476;
    /*
    *ABCD的临时变量
    */
    public static int Atemp,Btemp,Ctemp,Dtemp;
     
    /*
    *常量ti
    *公式:floor(abs(sin(i+1))×(2pow32)
    */
    public static final int K[]={
        0xd76aa478,0xe8c7b756,0x242070db,0xc1bdceee,
        0xf57c0faf,0x4787c62a,0xa8304613,0xfd469501,0x698098d8,
        0x8b44f7af,0xffff5bb1,0x895cd7be,0x6b901122,0xfd987193,
        0xa679438e,0x49b40821,0xf61e2562,0xc040b340,0x265e5a51,
        0xe9b6c7aa,0xd62f105d,0x02441453,0xd8a1e681,0xe7d3fbc8,
        0x21e1cde6,0xc33707d6,0xf4d50d87,0x455a14ed,0xa9e3e905,
        0xfcefa3f8,0x676f02d9,0x8d2a4c8a,0xfffa3942,0x8771f681,
        0x6d9d6122,0xfde5380c,0xa4beea44,0x4bdecfa9,0xf6bb4b60,
        0xbebfbc70,0x289b7ec6,0xeaa127fa,0xd4ef3085,0x04881d05,
        0xd9d4d039,0xe6db99e5,0x1fa27cf8,0xc4ac5665,0xf4292244,
        0x432aff97,0xab9423a7,0xfc93a039,0x655b59c3,0x8f0ccc92,
        0xffeff47d,0x85845dd1,0x6fa87e4f,0xfe2ce6e0,0xa3014314,
        0x4e0811a1,0xf7537e82,0xbd3af235,0x2ad7d2bb,0xeb86d391};
    /*
    *向左位移数,计算方法未知
    */
    public static final int s[]={7,12,17,22,7,12,17,22,7,12,17,22,7,
        12,17,22,5,9,14,20,5,9,14,20,5,9,14,20,5,9,14,20,
        4,11,16,23,4,11,16,23,4,11,16,23,4,11,16,23,6,10,
        15,21,6,10,15,21,6,10,15,21,6,10,15,21};
     
     
    /*
    *初始化函数
    */
    public static void init(){
        Atemp=A;
        Btemp=B;
        Ctemp=C;
        Dtemp=D;
    }
    public static void init_iv0(int myA, int myB, int myC, int myD){
        Atemp=myA;
        Btemp=myB;
        Ctemp=myC;
        Dtemp=myD;
    }
    /*
    *移动一定位数
    */
    public static    int    shift(int a,int s){
        return(a<<s)|(a>>>(32-s));//右移的时候，高位一定要补零，而不是补充符号位
    }
    /*
    *主循环
    */
    public static void MainLoop(int M[]){
        int F,g;
        int a=Atemp;
        int b=Btemp;
        int c=Ctemp;
        int d=Dtemp;
        for(int i = 0; i < 64; i ++){
            if(i<16){
                F=(b&c)|((~b)&d);
                g=i;
            }else if(i<32){
                F=(d&b)|((~d)&c);
                g=(5*i+1)%16;
            }else if(i<48){
                F=b^c^d;
                g=(3*i+5)%16;
            }else{
                F=c^(b|(~d));
                g=(7*i)%16;
            }
            int tmp=d;
            d=c;
            c=b;
            b=b+shift(a+F+K[i]+M[g],s[i]);
            a=tmp;
        }
        Atemp=a+Atemp;
        Btemp=b+Btemp;
        Ctemp=c+Ctemp;
        Dtemp=d+Dtemp;
     
    }
    /*
    *填充函数
    *处理后应满足bits≡448(mod512),字节就是bytes≡56（mode64)
    *填充方式为先加一个0,其它位补零
    *最后加上64位的原来长度
    */
    public static int[] add(String str){
        /*
        算成64B的整数倍，由于md5的长度位由8B表示, 所以需要多少个64B呢？ [(int(length + 8)) / 64] ([] 表示向上取整)
         */
        int num=((str.length()+8)/64)+1;//以512位，64个字节为一组

        int strByte[]=new int[num*16];//64/4=16，所以有16个整数
        for(int i=0;i<num*16;i++){//全部初始化0
            strByte[i]=0;
        }
        int    i;
        /*
        一个int由4B组成，little endian 中的shiw(一个int长度)为 wihs
        对于最后的ei补齐: ei(10(7个0))0(8个0), little endian 0(8个0)10(7个0)ie
         */
        for(i=0;i<str.length();i++){
            strByte[i>>2]|=str.charAt(i)<<((i%4)*8);//一个整数存储四个字节，小端序
        }
        strByte[i>>2]|=0x80<<((i%4)*8);//尾部添加1
        /*
        *添加原长度，长度指位的长度，所以要乘8，然后是小端序，所以放在倒数第二个,这里长度只用了32位
        *
        * 最后的长度位8B记录的是hash string bit的长度, little endian为int的 little endian
        */
        strByte[num*16-2]=str.length()*8;
            return strByte;
    }

    public static int[] add_hexStr(String hexStr){
        int length = hexStr.length()/2;
        int num=((length+8)/64)+1;//以512位，64个字节为一组

        int strByte[]=new int[num*16];//64/4=16，所以有16个整数
        for(int i=0;i<num*16;i++){//全部初始化0
            strByte[i]=0;
        }
        int    i;
        for(i=0;i<length;i++){
            short tmp = (short)Integer.parseInt(hexStr.substring(i * 2, i * 2 + 2), 16);
            strByte[i>>2]|=tmp<<((i%4)*8);//一个整数存储四个字节，小端序
        }
        strByte[i>>2]|=0x80<<((i%4)*8);//尾部添加1
        /*
        *添加原长度，长度指位的长度，所以要乘8，然后是小端序，所以放在倒数第二个,这里长度只用了32位
        *
        * 最后的长度位8B记录的是hash string bit的长度, little endian为int的 little endian
        */
        strByte[num*16-2]=length*8;
        return strByte;
    }
    /*
    *调用函数
    */
    public static String getMD5(String source){
        init();
        int strByte[]=add(source);
        for(int i=0;i<strByte.length/16;i++){
            int num[]=new int[16];
            for(int j=0;j<16;j++){
                num[j]=strByte[i*16+j];
            }
            MainLoop(num);
            System.out.println(changeHex(Atemp)+changeHex(Btemp)+changeHex(Ctemp)+changeHex(Dtemp));
        }
        return changeHex(Atemp)+changeHex(Btemp)+changeHex(Ctemp)+changeHex(Dtemp);
     
    }
    public static String getMD5_hexStr(String hexStr){
        init();
        int strByte[]=add_hexStr(hexStr);
        for(int i=0;i<strByte.length/16;i++){
            int num[]=new int[16];
            for(int j=0;j<16;j++){
                num[j]=strByte[i*16+j];
            }
            MainLoop(num);
            System.out.println(changeHex(Atemp)+changeHex(Btemp)+changeHex(Ctemp)+changeHex(Dtemp));

        }
        return changeHex(Atemp)+changeHex(Btemp)+changeHex(Ctemp)+changeHex(Dtemp);

    }

    public static String getMD5_hexStr_iv(String hexStr, int myA, int myB, int myC, int myD){
        init_iv0(myA, myB, myC, myD);
        int strByte[]=add_hexStr(hexStr);
        for(int i=0;i<strByte.length/16;i++){
            int num[]=new int[16];
            for(int j=0;j<16;j++){
                num[j]=strByte[i*16+j];
            }
            MainLoop(num);
            System.out.println(changeHex(Atemp)+changeHex(Btemp)+changeHex(Ctemp)+changeHex(Dtemp));

        }
        return changeHex(Atemp)+changeHex(Btemp)+changeHex(Ctemp)+changeHex(Dtemp);

    }
    /*
    *整数变成16进制字符串 每个B做 little endian
    */
    public static String changeHex(int a){
        String str="";
        for(int i=0;i<4;i++){
//            System.out.println(((a>>i*8)%(1<<8))&0xff);
            str+=String.format("%2s", Integer.toHexString(((a>>i*8)%(1<<8))&0xff)).replace(' ', '0');
 
        }
        return str;
    }

    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }

    public static String md5HexString(String hexStr){
        String str = "";

        int length = hexStr.length() / 2;
        for(int i = 0; i < length; i++){
            str += (char)Integer.parseInt(hexStr.substring(i * 2, i * 2 + 2), 16);
        }
        return getMD5(str);
    }

    /*
    规定初始的iv信息并且满足 f(f(iv0, m10), m11) = f(f(iv0, m00), m01)
    f 为 mainloop
     */
    public static void collisionBlocks2() throws Exception{
        /*
        f(f(iv0, m10), m11) = f(f(iv0, m00), m01)
         */
        int iv[] = {0xd123d11e, 0xef9a1b3b, 0x8ce1cab0, 0x4a2394c6};
        int m0[] = {
                0x20564c0f, 0x5abb6ef4, 0x772c7459, 0x984177b2,
                0xd57cd830, 0x2cc320eb, 0x8627b7c2, 0xd050edc2,
                0x0533ed51, 0xa334a003, 0x7e6eb1db, 0x4fc95ed2,
                0x8fec7cc2, 0x26ac5fbc, 0x10fcbd64, 0xead8ea1c,
                0xfa3aae6f, 0xeef7a50b, 0x2c7b01f8, 0x6c62920a,
                0x976e11be, 0xde09847f, 0x1531253e, 0xa02da74e,
                0x6bb37809, 0x4a2f60f1, 0x8405dbde, 0x8c8af4a5,
                0x7dc67cff, 0x9c4bc972, 0xa9191b8c, 0x5543951f,
        };
        int m1[] = {
                0x20564c0f, 0x5abb6ef4, 0x772c7459, 0x984177b2,
                0x557cd830, 0x2cc320eb, 0x8627b7c2, 0xd050edc2,
                0x0533ed51, 0xa334a003, 0x7e6eb1db, 0x4fc9ded2,
                0x8fec7cc2, 0x26ac5fbc, 0x90fcbd64, 0xead8ea1c,
                0xfa3aae6f, 0xeef7a50b, 0x2c7b01f8, 0x6c62920a,
                0x176e11be, 0xde09847f, 0x1531253e, 0xa02da74e,
                0x6bb37809, 0x4a2f60f1, 0x8405dbde, 0x8c8a74a5,
                0x7dc67cff, 0x9c4bc972, 0x29191b8c, 0x5543951f,
        };
        String hex1 = "";
        String hex2 = "";
        for(int i = 0; i < m0.length; i++){
            String tmp = changeHex(m0[i]);
            hex1 += tmp;
        }
        System.out.println(hex1);
        System.out.println(hex1.length());
        for(int i = 0; i < m1.length; i++){
            String tmp = changeHex(m1[i]);
            hex2 += tmp;
        }
        System.out.println(hex2);
        System.out.println(hex2.length());
        System.out.println("hex1");
        System.out.println(getMD5_hexStr_iv(hex1, iv[0], iv[1], iv[2], iv[3]));
        System.out.println("hex2");
        System.out.println(getMD5_hexStr_iv(hex2, iv[0], iv[1], iv[2], iv[3]));
    }

    public static void collisionBlocks() throws Exception{
        String hex1 = "d131dd02c5e6eec4693d9a0698aff95c2fcab58712467eab4004583eb8fb7f8955ad340609f4b30283e488832571415a085125e8f7cdc99fd91dbdf280373c5bd8823e3156348f5bae6dacd436c919c6dd53e2b487da03fd02396306d248cda0e99f33420f577ee8ce54b67080a80d1ec69821bcb6a8839396f9652b6ff72a70";
        String hex2 = "d131dd02c5e6eec4693d9a0698aff95c2fcab50712467eab4004583eb8fb7f8955ad340609f4b30283e4888325f1415a085125e8f7cdc99fd91dbd7280373c5bd8823e3156348f5bae6dacd436c919c6dd53e23487da03fd02396306d248cda0e99f33420f577ee8ce54b67080280d1ec69821bcb6a8839396f965ab6ff72a70";
        System.out.println("hex1");
        System.out.println(getMD5_hexStr(hex1));
        System.out.println("hex2");
        System.out.println(getMD5_hexStr(hex2));
    }
     /*
     MD5加密字符串实例
    现以字符串“jklmn”为例。
    该字符串在内存中表示为：6A 6B 6C 6D 6E（从左到右为低地址到高地址，后同），信息长度为40 bits， 即0x28。
    对其填充，填充至448位，即56字节。结果为：
    6A 6B 6C 6D 6E 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
    剩下64位，即8字节填充填充前信息位长，按小端字节序填充剩下的8字节，结果为。
    6A 6B 6C 6D 6E 80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 28 00 00 00 00 00 00 00
    （64字节，512 bits）
    初始化A、B、C、D四个变量。
    将这64字节填充后数据分成16个小组（程序中对应为16个数组），即：
    M0：6A 6B 6C 6D （这是内存中的顺序，按照小端字节序原则，对应数组M(0)的值为0x6D6C6B6A，下同）
    M1：6E 80 00 00
    M2：00 00 00 00
    .....
    M14：28 00 00 00
    M15：00 00 00 00
    经过“3. 分组数据处理”后，a、b、c、d值分别为0xD8523F60、0x837E0144、0x517726CA、0x1BB6E5FE
    在内存中为a：60 3F 52 D8
    b：44 01 7E 83
    c：CA 26 77 51
    d：FE E5 B6 1B
    a、b、c、d按内存顺序输出即为最终结果：603F52D844017E83CA267751FEE5B61B。这就是字符串“jklmn”的MD5值。
      */
    public static void main(String[] args) throws Exception{
//        System.out.println(MD5.getMD5("abcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcabcab"));
//        collisionBlocks();
        collisionBlocks2();
    }
}