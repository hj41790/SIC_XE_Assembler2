import java.util.ArrayList;

public class SectionTable {

	private static SectionTable table = null;
	
	private ArrayList<Section> array = null;
	private int locctr;
	private int sectionNum;
	
	public static SectionTable getInstance(){
		
		if(table == null)
			table = new SectionTable();
		
		return table;
	}
	
	private SectionTable(){
		array = new ArrayList<Section>();
		locctr = 0;
		sectionNum = 0;
	}	
	
	public int getSectionNum(){
		return sectionNum;
	}
	
	public void nextSection(){
		sectionNum++;
	}
	
	public int getLocctr(){
		return locctr;
	}
	
	public void addLocctr(int offset){
		locctr += offset;
	}
	
	public void setLocctr(int l){
		locctr = l;
	}
	
}
