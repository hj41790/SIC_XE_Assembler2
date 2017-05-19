import java.util.ArrayList;

public class ModifyTable {
	private static ModifyTable table = null;

	private ArrayList<Modification> array = null;

	public static ModifyTable getInstance(){

		if(table == null)
			table = new ModifyTable();

		return table;
	}

	private ModifyTable(){

		array = new ArrayList<Modification>();

	}	
	
	public void addModifyTable(Modification t){
		
		try{
			if(t==null)
				throw new CustomException(CustomException.NULL_POINTER_EXCEPTION);
			
			array.add(t);
			
		}catch(CustomException e){
			e.printStackTrace();
		}
	}
	
	public void setLocation(Token t, int location){
		
		for(Modification m : array){
			
			if(m.checkToken(t)){
				m.setLocation(location);
			}
			
		}
		
	}
	
	public String toString(int section){
		
		String result = "";
		
		for(Modification m : array){
			
			if(m.checkSection(section)){
				result += m.toString() + "\n";
			}
			
		}
		
		return result;
	}
	
}
