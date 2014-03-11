package magiccollege.net;

public class Battle {
	private String p0;
	private String p1;
	private int p0deskSize;
	private int p1deskSize;
	
	public Battle(final String pp0, final String pp1){
		p0 = pp0;
		p1 = pp1;
	}
	
	public void setP0deskSize(final int size){
		p0deskSize = size;
	}
	
	public void setP1deskSize(final int size){
		p1deskSize = size;
	}
	
	public int getP0deskSize(){
		return p0deskSize;
	}
	
	public int getP1deskSize(){
		return p1deskSize;
	}
	
	public String getP1(){
		return p1;
	}
	
	public String getP0(){
		return p0;
	}
	
	public String getOthersID(final String myID){
		if (p0.equals(myID)){
			return p1;
		}
		else
			return p0;
	}
}
