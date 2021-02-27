package xworker.util;

public class UtilNumber {
	public static String numberToChinese(double n){
		String fraction[] = {"角", "分", "厘"};
        String digit[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
        String unit[][] = {{"元", "万", "亿"},
                     {"", "拾", "佰", "仟"}};
 
        String head = n < 0? "负": "";
        n = Math.abs(n);
         
        String s = "";
        for (int i = 0; i < fraction.length; i++) {
            s += (digit[(int)(Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
        }
        if(s.length()<1){
            s = "整";    
        }
        int integerPart = (int)Math.floor(n);
 
        for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
            String p ="";
            for (int j = 0; j < unit[1].length && n > 0; j++) {
                p = digit[integerPart%10]+unit[1][j] + p;
                integerPart = integerPart/10;
            }
            s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
        }
        return head + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
	}
	
	public static void main(String agrs[]) {
        //整数
        System.out.println(numberToChinese(0));              // 零元整
        System.out.println(numberToChinese(123));            // 壹佰贰拾叁元整
        System.out.println(numberToChinese(1000000));        // 壹佰万元整
        System.out.println(numberToChinese(100000001));      // 壹亿零壹元整
        System.out.println(numberToChinese(1000000000));     // 壹拾亿元整
        System.out.println(numberToChinese(1234567890));     // 壹拾贰亿叁仟肆佰伍拾陆万柒仟捌佰玖拾元整
        System.out.println(numberToChinese(1001100101));     // 壹拾亿零壹佰壹拾万零壹佰零壹元整
        System.out.println(numberToChinese(110101010));      // 壹亿壹仟零壹拾万壹仟零壹拾元整
     
        //小数
        System.out.println(numberToChinese(0.1332));          // 壹角贰分
        System.out.println(numberToChinese(123.3433));        // 壹佰贰拾叁元叁角肆分
        System.out.println(numberToChinese(1000000.5336));    // 壹佰万元伍角陆分
        System.out.println(numberToChinese(1000000000.9330)); // 壹拾亿元玖角
        System.out.println(numberToChinese(1234567890.03333)); // 壹拾贰亿叁仟肆佰伍拾陆万柒仟捌佰玖拾元叁分
        System.out.println(numberToChinese(1001100101.0033)); // 壹拾亿零壹佰壹拾万零壹佰零壹元整
        System.out.println(numberToChinese(110101010.1330));  // 壹亿壹仟零壹拾万壹仟零壹拾元壹角
         
        //负数
        System.out.println(numberToChinese(-0.12));          // 负壹角贰分
        System.out.println(numberToChinese(-123.34));        // 负壹佰贰拾叁元叁角肆分
        System.out.println(numberToChinese(-1000000.56));    // 负壹佰万元伍角陆分
        System.out.println(numberToChinese(-100000001.78));  // 负壹亿零壹元柒角捌分
        System.out.println(numberToChinese(-1000000000.90)); // 负壹拾亿元玖角
        System.out.println(numberToChinese(-1234567890.03)); // 负壹拾贰亿叁仟肆佰伍拾陆万柒仟捌佰玖拾元叁分
        System.out.println(numberToChinese(-1001100101.00)); // 负壹拾亿零壹佰壹拾万零壹佰零壹元整
        System.out.println(numberToChinese(-110101010.10));  // 负壹亿壹仟零壹拾万壹仟零壹拾元壹角
    }
}
