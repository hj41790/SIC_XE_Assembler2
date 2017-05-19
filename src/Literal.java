
public class Literal {
	
	private String 	name;
	private int		section;
	private int 	length;
	private int 	addr;

	
	public Literal(String s, int _section){
		
		// calculate length
		
		name = s;
		section = _section;
		addr = 0;
	}
	
	public void setAddr(int _addr){
		addr = _addr;
	}

	public boolean equals(String name2, int section2) {
		// TODO Auto-generated method stub
		
		if(name==name2 && section==section2) return true;
		
		return false;
	}
}
