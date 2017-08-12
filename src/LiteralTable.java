import java.util.ArrayList;
import java.util.regex.Pattern;

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
			if(name == null) 
				throw new CustomException(CustomException.NULL_POINTER_EXCEPTION);
			
			for(Literal l : array){
				if(l.equals(name, section)) return;
			}
			
			// length
			int length = 0;
			String pattern1 = "^=C\'(\\s|\\S)*\'$";			// =C'__'
			String pattern2 = "^=X\'([0-9]|[A-F]){2}\'$";	// =X'__'
			String pattern3 = "^=[0-9]*$";
			
			if(Pattern.matches(pattern1, name)){
				length = name.length()-4;
			}
			else if(Pattern.matches(pattern2, name)){
				length = (name.length()-3)/2;
			}
			else if(Pattern.matches(pattern3, name)){
				length = 3;
			}
			else
				throw new CustomException(CustomException.WRONG_FORMAT);
			
			Literal l = new Literal(name, section, length);
			array.add(l);
		}
		catch(CustomException e){
			e.printStackTrace();
		}
		
	}
	
	public Literal search(String name, int section){
		for(Literal l : array){
			if(l.equals(name, section)) return l;
		}
		return null;
	}
	
	public void LTORG(){
		
		SectionTable SECTAB = SectionTable.getInstance();
		
		for(Literal l : array){
			
			if(l.getAddr()<0){
				
				l.setAddr(SECTAB.getLocctr());
				
				Token token = new Token();
				token.setAddr(SECTAB.getLocctr());
				token.setLabel("*");
				token.setOperator(l.getName());
				token.setSection(l.getSection());
				token.setOptype(Token.TYPE_LITERAL);
				
				SECTAB.addLocctr(l.getLength());
				TokenTable.getInstance().addToken(token);
			}
			
		}
		
	}
	
	public void print(){
		for(Literal l : array){
			l.print();
		}
		
	}
	
}
