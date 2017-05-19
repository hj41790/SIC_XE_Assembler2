
public class Symbol {
	
	private String 	name;
	private int		section;
	private int		addr;
	private int 	refSection;
	
	private boolean EXTDEF;
	private boolean EQU;
	private boolean EXIST;

	public Symbol(String n, int s, int a){
		name = n;
		section = s;
		addr = a;
		refSection = 0;
		
		EXTDEF = false;
		EQU = false;
		EXIST = false;
	}
	
	
	
	public void setSection(int section) {
		this.section = section;
	}



	public void setAddr(int addr) {
		this.addr = addr;
	}



	public int getAddr() {
		return addr;
	}



	public boolean compareName(String n){
		return name.equals(n);
	}

	public boolean isEXTDEF() {
		return EXTDEF;
	}

	public void setEXTDEF() {
		EXTDEF = true;
	}

	public boolean isEQU() {
		return EQU;
	}

	public void setEQU() {
		EQU = true;
	}

	public boolean isEXIST() {
		return EXIST;
	}

	public void setEXIST() {
		EXIST = true;
	}

	public int getSection() {
		return section;
	}

	public void setRefSection(int _refSection) {
		int mask = 1 << _refSection;
		refSection |= mask;
	}



	public boolean isReference(int s) {
		// TODO Auto-generated method stub
		
		int mask = 1<<s;

		if((refSection & mask) > 0) return true;
		
		return false;
	}
	
	public void print(){
		
		System.out.format("%-10s sect:%5d addr : %6X ref : %d\n", name, section, addr, refSection);
		
	}
	
}
