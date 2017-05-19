import java.util.ArrayList;

public class LiteralTable {

	private static LiteralTable table = null;
	
	private ArrayList<Literal> array = null;
	
	public static LiteralTable getInstance(){
		
		if(table == null)
			table = new LiteralTable();
		
		return table;
	}
	
	private LiteralTable(){
		
		array = new ArrayList<Literal>();
		
	}	
	
	public void addLiteral(String name, int section){
		try{
			if(name == null) throw new CustomException(CustomException.NULL_POINTER_EXCEPTION);
			if(section < 0) throw new CustomException(CustomException.PARAMETER_WORNG_FORMAT);
			
			for(Literal l : array){
				if(l.equals(name, section)) return;
			}

			Literal l = new Literal(name, section);
			array.add(l);
		}
		catch(CustomException e){
			e.printStackTrace();
		}
		
	}
	
}
