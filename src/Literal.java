
public class Literal {
	
	private String 	name;
	private int		section;
	private int 	length;
	private int 	addr;

	
	public Literal(String s, int _section, int _length){
		
		// calculate length
		
		name = s;
		section = _section;
		length = _length;
		addr = -1;
	}
	
	public void setAddr(int _addr){
		addr = _addr;
	}

	public int getSection() {
		return section;
	}

	public void setSection(int section) {
		this.section = section;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getAddr() {
		return addr;
	}

	public String getName() {
		return name;
	}
	
	public void print(){
		System.out.format("%-10s %-3d %-3d %d\n", name, section, length, addr);
	}

	public boolean equals(String name2, int section2) {
		// TODO Auto-generated method stub
			
		if(name.equals(name2) && section==section2) return true;
		
		return false;
	}
}
