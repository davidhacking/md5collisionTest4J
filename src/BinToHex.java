import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class BinToHex
{
    private final static String[] hexSymbols = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    public final static int BITS_PER_HEX_DIGIT = 4;

    public static String toHexFromByte(final byte b)
    {
    	byte leftSymbol = (byte)((b >>> BITS_PER_HEX_DIGIT) & 0x0f);
    	byte rightSymbol = (byte)(b & 0x0f);

    	return (hexSymbols[leftSymbol] + hexSymbols[rightSymbol]);
    }

    public static String toHexFromBytes(final byte[] bytes, int value)
    {
    	if(value <= 0 || bytes == null || bytes.length == 0)
    	{
    		return ("");
    	}

    	// there are 2 hex digits per byte
    	StringBuilder hexBuffer = new StringBuilder(value * 2);

    	// for each byte, convert it to hex and append it to the buffer
    	for(int i = 0; i < value; i++)
    	{
    		hexBuffer.append(toHexFromByte(bytes[i]));
    	}

    	return (hexBuffer.toString());
    }

	public static String getHexString(String file){
		String hexStr = "";
		try
		{
			FileInputStream fis = new FileInputStream(new File(file));
			byte[] bytes = new byte[800];
			int value = 0;
			do
			{
				Arrays.fill( bytes, (byte) 0 );
				value = fis.read(bytes);
				hexStr += toHexFromBytes(bytes, value);

			}while(value != -1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return hexStr;
	}

	public static void myCompareString(String a, String b){
		System.out.println("a.length: " + a.length());
		System.out.println("b.length: " + b.length());
		int minLen = Math.min(a.length(), b.length());
		for (int i = 0 ; i != minLen ; i++) {
			char chA = a.charAt(i);
			char chB = b.charAt(i);
			if (chA != chB) {
				String out = String.format("index: %d, '%c' in a | '%c' in b", i, chA, chB);
				System.out.println(out);
			}
		}
	}

    public static void main(final String[] args) throws IOException
    {
		String hello = getHexString("F:\\GraduateStep1\\algorithm\\hello.exe");
		String erase = getHexString("F:\\GraduateStep1\\algorithm\\erase.exe");
		String shiwei = getHexString("F:\\GraduateStep1\\algorithm\\shiwei.txt");
		System.out.println(shiwei);
		myCompareString(hello, erase);
		System.out.println("hello md5: " + MD5.getMD5_hexStr(hello));
		System.out.println("erase md5: " + MD5.getMD5_hexStr(erase));
		System.out.println("shiwei md5: " + MD5.getMD5_hexStr(shiwei));
	}
}