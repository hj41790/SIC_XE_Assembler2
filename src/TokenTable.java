import java.util.ArrayList;

public class TokenTable {
	
	private static TokenTable table = null;

	private ArrayList<Token> array = null;

	public static TokenTable getInstance(){

		if(table == null)
			table = new TokenTable();

		return table;
	}

	private TokenTable(){

		array = new ArrayList<Token>();

	}	
	
	public void addToken(Token t){
		
		try{
			if(t==null)
				throw new CustomException(CustomException.NULL_POINTER_EXCEPTION);
			
			array.add(t);
			
		}catch(CustomException e){
			e.printStackTrace();
		}
	}
	
	public void intermediate_print(){
		
		for(Token t : array){
			System.out.println(t.toString());
		}
		
	}
}
