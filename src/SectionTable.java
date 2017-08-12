import java.util.ArrayList;

public class SectionTable {

	private static SectionTable table = null;
	
	private ArrayList<Section> array = null;
	private int return_addr;
	private int start_addr;
	private int locctr;
	private int sectionNum;
	
	public static SectionTable getInstance(){
		
		if(table == null)
			table = new SectionTable();
		
		return table;
	}
	
	private SectionTable(){
		array = new ArrayList<Section>();
		return_addr = 0;
		start_addr = 0;
		locctr = 0;
		sectionNum = -1;
	}	
	
	public void addSection(Section s){
		
		try {
			
			if(s==null)
				throw new CustomException(CustomException.NULL_POINTER_EXCEPTION);

			sectionNum++;
			array.add(s);
			
		} catch (CustomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public int getSectionNum(){
		return sectionNum;
	}
	
	public int getLocctr(){
		return locctr;
	}
	
	public void addLocctr(int offset){
		locctr += offset;
	}
	
	public void setLocctr(int l){
		start_addr = l;
		locctr = l;
	}
	
	public int getReturn_addr() {
		return return_addr;
	}

	public void setReturn_addr(int return_addr) {
		this.return_addr = return_addr;
	}

	public void setLength(){
		Section s = array.get(sectionNum);
		s.setLength(locctr - start_addr);
		
//		System.out.println("\n\n"+String.format("%X", locctr-start_addr));
	}
	
	public int getLength(int section){
		return array.get(section).getLength();
	}
	
}
