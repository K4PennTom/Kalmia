import java.awt.Color;
import java.util.Hashtable;


public class TempColorGenerator {
	Hashtable<Integer, Color> colors = new Hashtable<Integer,Color>();
	
	
	public TempColorGenerator(){
		colors.put(-5, new Color(255,255,255));//
		colors.put(0, new Color(230,153,255));//
		colors.put(5, new Color(204,51,255));//
		colors.put(10, new Color(204,82,255));//
		colors.put(15, new Color(204,112,255));//
		colors.put(20, new Color(204,143,255));//
		colors.put(25, new Color(204,173,255));//
		colors.put(30, new Color(204,204,255));//
		colors.put(35, new Color(102,255,255));//
		colors.put(40, new Color(0,102,204));//
		colors.put(45, new Color(0,156,133));//
		colors.put(50, new Color(0,201,71));//
		colors.put(55, new Color(0,255,0));//
		colors.put(60, new Color(128,255,0));//
		colors.put(65, new Color(255,255,0));//
		colors.put(70, new Color(255,170,0));//
		colors.put(75, new Color(255,102,0));//
		colors.put(80, new Color(245,82,20));//
		colors.put(85, new Color(235,61,41));//
		colors.put(90, new Color(224,41,61));//
		colors.put(95, new Color(214,20,82));//
		colors.put(100, new Color(204,0,102));//
		colors.put(105, new Color(214,51,133));//
		colors.put(110, new Color(224,102,163));//
		colors.put(115, new Color(235,153,194));//
		colors.put(120, new Color(245,204,224));//
	}
	public Color calcColor(Double temp){
		Integer spacing = 5;
		if((temp<-5)||
		   (temp>120)){
			return Color.gray;
		}
		Integer upperTemp = 0;
		Integer lowerTemp = 0;
		if(temp>=0){
			upperTemp = (int) (temp/spacing+1)*spacing;
			lowerTemp = (int) (temp/spacing)*spacing;
		}else{
			upperTemp = (int) ((temp+0.1)/spacing)*spacing;
			lowerTemp = (int) ((temp+0.1)/spacing-1)*spacing;
		}
		//System.out.println("Temp:" + temp + " Upper: " + upperTemp +" Lower: "+ lowerTemp );
		Color upperColor = colors.get(upperTemp);
		Color lowerColor = colors.get(lowerTemp);
		
		Double spread = (temp-lowerTemp)/spacing.doubleValue();
		//System.out.print("Temp:" + temp + " Upper: " + upperTemp +" Lower: "+ lowerTemp);
		int red = (int) Math.round((upperColor.getRed() - lowerColor.getRed())*spread+lowerColor.getRed());
		int green = (int) Math.round((upperColor.getGreen() - lowerColor.getGreen())*spread+lowerColor.getGreen());
		int blue = (int) Math.round((upperColor.getBlue() - lowerColor.getBlue())*spread+lowerColor.getBlue());
		
		//System.out.print(" RGB("+red+"," +green+","+blue+") spread: " +spread+"\n");
		return new Color(red,green,blue);
	}
}
