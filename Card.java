
public class Card {
	String function;
	String other;
	String dir;
	Card(String function, String other, String dir){
		this.function=function;
		this.other=other;
		this.dir=dir;
	}
	
	public String getFunc() {
		return function;
	}
	
	public String getOther() {
		return other;
	}
	
	public String getDir() {
		return dir;
	}
}
