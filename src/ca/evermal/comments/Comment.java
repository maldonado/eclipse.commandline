package ca.evermal.comments;

import gr.uom.java.ast.CommentObject;
import gr.uom.java.ast.CommentType;
import gr.uom.java.ast.ConstructorObject;
import gr.uom.java.ast.FieldObject;
import gr.uom.java.ast.MethodObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ca.evermal.util.ConnectionFactory;

public class Comment {

	private static int LINE_CORRECTION = 1;

	private long id;
	private long classCommentId;
	private int startLine;
	private int endLine;
	private String text;
	private CommentType type;
	private String location;
	private String description;
	private int dictionaryHit;
	private int jdeodorantHit;
	private String refactoringListName;

	public Comment(){};
	
	public Comment(FieldObject field, CommentObject commentObject) {
		this.setStartLine(commentObject.getStartLine());
		this.setEndLine(commentObject.getEndLine());
		this.text = commentObject.getText();
		this.type = commentObject.getType();
		this.description = field.getName();
		this.location = "FIELD";
	}

	public Comment(MethodObject method, CommentObject commentObject) {
		this.setStartLine(commentObject.getStartLine());
		this.setEndLine(commentObject.getEndLine());
		this.text = commentObject.getText();
		this.type = commentObject.getType();
		this.description = method.getSignature();
		this.location = "METHOD";
	}

	public Comment(ConstructorObject method, CommentObject commentObject) {
		this.setStartLine(commentObject.getStartLine());
		this.setEndLine(commentObject.getEndLine());
		this.text = commentObject.getText();
		this.type = commentObject.getType();
		this.description = method.getSignature();
		this.location = "CONSTRUCTOR";
	}

	public Comment(String className, CommentObject commentObject) {
		this.setStartLine(commentObject.getStartLine());
		this.setEndLine(commentObject.getEndLine());
		this.text = commentObject.getText();
		this.type = commentObject.getType();
		this.description = className;
		this.location = "CLASS";
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setClassCommentId(long classCommentId) {
		this.classCommentId = classCommentId;
	}

	public long getClassCommentId() {
		return this.classCommentId;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setType(CommentType type) {
		this.type = type;
	}

	public CommentType getType() {
		return type;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine + LINE_CORRECTION;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine + LINE_CORRECTION;
	}
	
	public void setStartLineWitoutCorrection(int startLine) {
		this.startLine = startLine;
	}

	public void setEndLineWitoutCorrection(int endLine) {
		this.endLine = endLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public int getStartLine() {
		return startLine;
	}
	
	public int getJdeodorantHit() {
		return jdeodorantHit;
	}

	public void setJdeodorantHit(int jdeodorantHit) {
		this.jdeodorantHit = jdeodorantHit;
	}

	public String getRefactoringListName() {
		return refactoringListName;
	}

	public void setRefactoringListName(String refactoringListName) {
		this.refactoringListName = refactoringListName;
	}
	
	public int getDictionaryHit() {
		return dictionaryHit;
	}

	public void setDictionaryHit(int dictionaryHit) {
		this.dictionaryHit = dictionaryHit;
	}

	public void insertProcessed() {
		executeInsert(ConnectionFactory.getSqlite(), this.classCommentId, "INSERT INTO processed_comment (commentClassId, startLine, endLine, commentText, type, location, description, dictionary_hit, jdeodorant_hit, refactoring_list_name) values(?,?,?,?,?,?,?,?,?,?)");
	}
	
	public void insert(Connection dataBaseConnection, long classCommentId) {
		executeInsert(dataBaseConnection, classCommentId, "INSERT INTO comment (commentClassId, startLine, endLine, commentText, type, location, description, dictionary_hit, jdeodorant_hit, refactoring_list_name) values(?,?,?,?,?,?,?,?,?,?)");
	}

	private void executeInsert(Connection dataBaseConnection, long classCommentId, String sql) {
		try {
			PreparedStatement preparedStatement = dataBaseConnection.prepareStatement(sql);
			preparedStatement.setLong(1, classCommentId);
			preparedStatement.setInt(2, this.startLine);
			preparedStatement.setInt(3, this.endLine);
			preparedStatement.setString(4, this.text);
			preparedStatement.setString(5, this.type.toString());
			preparedStatement.setString(6, this.location);
			preparedStatement.setString(7, this.description);
			preparedStatement.setInt(8, this.dictionaryHit);
			preparedStatement.setInt(9, this.jdeodorantHit);
			preparedStatement.setString(10, this.refactoringListName);
			preparedStatement.execute();
//			this.id = preparedStatement.getGeneratedKeys().getLong(1);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void delete() {
		executeDelete("DELETE from comment where id=?");
	}

	public void deleteProcessed(){
		executeDelete("DELETE from processed_comment where id=?");
	}
	
	private void executeDelete(String sql) {
		try {
			Connection dataBaseConnection = ConnectionFactory.getSqlite();
			PreparedStatement preparedStatement = dataBaseConnection .prepareStatement(sql);
			preparedStatement.setLong(1, this.id);
			preparedStatement.execute();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void update() {
		executeUpdate("UPDATE comment set commentClassId=?, startLine=?, endLine=?, commentText=?, type=?, location=?, description=?, dictionary_hit=?, jdeodorant_hit=?, refactoring_list_name=? where id =?");
	}
	
	public void updateProcessed() {
		executeUpdate("UPDATE processed_comment set commentClassId=?, startLine=?, endLine=?, commentText=?, type=?, location=?, description=?, dictionary_hit=?, jdeodorant_hit=?, refactoring_list_name=? where id =?");
	}

	private void executeUpdate(String sql) {
		Connection dataBaseConnection = ConnectionFactory.getSqlite();
		try{
			PreparedStatement preparedStatement = dataBaseConnection .prepareStatement(sql);
			preparedStatement.setLong(1, this.classCommentId);
			preparedStatement.setInt(2, this.startLine);
			preparedStatement.setInt(3, this.endLine);
			preparedStatement.setString(4, this.text);
			preparedStatement.setString(5, this.type.toString());
			preparedStatement.setString(6, this.location);
			preparedStatement.setString(7, this.description);
			preparedStatement.setInt(8, this.dictionaryHit);
			preparedStatement.setInt(9, this.jdeodorantHit);
			preparedStatement.setString(10, this.refactoringListName);
			preparedStatement.setLong(11, this.id);
			preparedStatement.execute();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	public static ArrayList<Comment> findByCommentClassId(Connection dataBaseConnection, long classCommentId) {
		return findByCommentClassID(dataBaseConnection, classCommentId, "SELECT * FROM comment where commentClassId = ? order by endLine");
	}
	
	public static ArrayList<Comment> findProcessedByCommentClassId(long classCommentId) {
		return findByCommentClassID(ConnectionFactory.getSqlite(), classCommentId, "SELECT * FROM processed_comment where commentClassId = ? order by endLine");
	}
	
	public static ArrayList<Comment> findProcessedByCommentClassId(Connection dataBaseConnection, long classCommentId) {
		return findByCommentClassID(dataBaseConnection, classCommentId, "SELECT * FROM processed_comment where commentClassId = ? order by endLine");
	}

	private static ArrayList<Comment> findByCommentClassID(Connection dataBaseConnection, long classCommentId, String sql) {
		ArrayList<Comment> comments = new ArrayList<Comment>();
		try{
			PreparedStatement preparedStatement = dataBaseConnection.prepareStatement(sql);
			preparedStatement.setLong(1, classCommentId);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				Comment comment = new Comment();
				comment.setId(resultSet.getLong("id"));
				comment.setClassCommentId(resultSet.getLong("commentClassId"));
				comment.setText(resultSet.getString("commentText"));
				comment.setType(CommentType.valueOf(resultSet.getString("type")));
				comment.setLocation(resultSet.getString("location"));
				comment.setDescription(resultSet.getString("description"));
				comment.setStartLineWitoutCorrection(resultSet.getInt("startLine"));
				comment.setEndLineWitoutCorrection(resultSet.getInt("endLine")); 
				comment.setDictionaryHit(resultSet.getInt("dictionary_hit"));
				comment.setJdeodorantHit(resultSet.getInt("jdeodorant_hit"));
				comment.setRefactoringListName(resultSet.getString("refactoring_list_name"));
				comments.add(comment);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return comments;
	}

	public static void MatchDictionary() {
		String sql = "update processed_comment set dictionary_hit = 1 where commentText like '%future%may%' or commentText like '%future%better%' or commentText like '%future%enchance%' or commentText like '%future%change%' or commentText like '%quick%fix%' or commentText like '%temporary%until%' or commentText like '%place%somewhere%else%' or commentText like '%move%somewhere%else%'  "
				+ " or commentText like '%used%other%place%' or commentText like '%it %may %change %' or commentText like '%this may change%' or commentText like '%todo%can%change%' or commentText like '%fixme%can%change%' or commentText like '%xxx%can%change%'  or commentText like '%not %sure %'  or commentText like '%dependency%cycle%' or commentText like '%code%cop%from%' or "
				+ " commentText like '%copied%code%'  or commentText like '% any %reason%' or commentText like '%wrong%place%'  or  commentText like '%hairy%'  or  commentText like '%instead%could%'  or  commentText like '%ugly%' or  commentText like '%todo%avoid%' or commentText like '%fixme%avoid%' or commentText like '%xxx%avoid%' or commentText like '%should%avoid%' or  "
				+ " commentText like '%pathological%'  or commentText like '%stolen%'  or commentText like '%not%well%formed%'  or commentText like '% no %sense%since%'  or commentText like '%without%notic%'  or commentText like '%brittle%'  or commentText like '%really%necessary%'  or commentText like '%cares%'  or commentText like '%no idea%' or commentText like '%idea?%' or "
				+ " commentText like '%doing?%'  or commentText like '%todo%elsewhere%' or commentText like '%fixme%elsewhere%' or commentText like '%xxx%elsewhere%' or commentText like '%perhaps%elsewhere%'  or commentText like '%rather%complex%' or commentText like '%held%?%'  or commentText like '%though%unused%'  or commentText like '%todo%don%know%' or commentText like '%fixme%don%know%' "
				+ " or commentText like '%xxx%don%know%'or commentText like '%don%know%try%' or commentText like '%don%know%fail%' or commentText like '%don%know%what%' or commentText like '%don%know%fix%'  or commentText like '%not%fond%' or commentText like '%more%elegant%'  or commentText like '%clean%way%'  or commentText like '%todo%remove%' or commentText like '%xxx%remove%' "
				+ " or commentText like '%fixme%remove%'  or commentText like '%todo%don%want%' or commentText like '%fixme%don%want%' or commentText like '%xxx%don%want%'  or commentText like '% fix % for %'  or commentText like '%irritating%'  or commentText like '%todo%duplicat%' or commentText like '%fixme%duplicat' or commentText like '%xxx%duplicat%'  or commentText like '%why%not%' "
				+ " or commentText like '%rethink%' or commentText like '%rework%' or commentText like '%pointless%'  or commentText like '% not %nice%'  or commentText like '%hack%'  or commentText like '%only%developer%know%'  or commentText like '% use % help%' or commentText like '%hammer%'  or commentText like '%todo%redundant%' or commentText like '%fixme%redundant%' "  
				+ " or commentText like '%xxx%redundant%'  or commentText like '%for%some%reason%' or commentText like '%alternatively%could%'  or commentText like '%technically%'  or commentText like '% forces %us%' or commentText like '%better%way%' or commentText like '%hard%coded%' or commentText like '%hard%coding%'  or commentText like '%kludge%'  or commentText like '%todo%public%' "
				+ " or commentText like '%fixme%public%' or commentText like '%xxx%public%'  or commentText like '%messy%'  or commentText like '% fix for %' or commentText like '%should%instead%' or commentText like '%this%weird%' or  commentText like '%weird%this%' or  commentText like '%todo%weird%' or  commentText like '%fixme%weird%' or  commentText like '%xxx%weird%'  or  commentText like '%todo%availability%'"  
				+ " or commentText like '%fixme%availability%' or commentText like '%xxx%availability%' or  commentText like '%todo%extensibility%' or commentText like '%fixme%extensibility%' or commentText like '%xxx%extensibility%' or   commentText like '%sacrifice%flexibility%' or commentText like '%todo%flexibility%' or commentText like '%fixme%flexibility%' "
				+ " or commentText like '%xxx%flexibility%' or  commentText like '%todo%scalability%' or commentText like '%fixme%scalability%' or commentText like '%xxx%scalability%' or  commentText like '%security%compatibility%' or commentText like '%security%never%' or commentText like '%todo%security%' or commentText like '%fixme%security%' or commentText like '%xxx%security%' "
				+ " or commentText like '%todo%ambiguous%' or commentText like '%fixme%ambiguous%' or commentText like '%xxx%ambiguous%' or commentText like '%todo% big %' or commentText like '%fixme% big %' or commentText like '%xxx% big %' or commentText like '% big % mess %' or  commentText like '%clean%needed%' or commentText like '%should%clean%' or commentText like '%todo%clean%' "
				+ " or commentText like '%fixme%clean%' or commentText like '%xxx%clean%' or   commentText like '%due%complex%' or '%way%complex%' or commentText like '%todo%complex%' or commentText like '%fixme%complex%' or commentText like '%xxx%complex%' or   commentText like '%consistency%sake%' or   commentText like '% lack %broke%' or commentText like '% lack %problem%' "
				+ " or commentText like '% lack %should%' or  commentText like '%todo% lack %' or  commentText like '%fixme% lack %' or  commentText like '%xxx% lack %' or  commentText like '%todo% long %' or commentText like '%fixme% long %' or commentText like '%xxx% long %' or  commentText like '%todo% large %' or commentText like '%fixme% large %' or commentText like '%xxx% large %' "
				+ " or commentText like '%future%maintenance%' or commentText like '%todo%maintenance%' or commentText like '%fixme%maintenance%' or commentText like '%xxx%maintenance%' or   commentText like '%todo%unused%'or commentText like '%fixme%unused%' or commentText like '%xxx%unused%' or commentText like '%currently%unused%' or commentText like '%unused%delete%' "
				+ " or commentText like '%unused%currently%' or   commentText like '%such%bad%' or commentText like '%todo%bad%'  or commentText like '%fixme%bad%' or commentText like '%xxx%bad%' or   commentText like '%todo%clone%code%' or commentText like '%fixme%clone%code%' or commentText like '%xxx%clone%code%' or   commentText like '% dead %code%' "
				+ " or  commentText like '%todo%dependenc%' or commentText like '%fixme%dependenc%' or commentText like '%xxx%dependenc%' or   commentText like '%crappy%design%' or commentText like '%design%flaw%' or commentText like '% todo% design %' or commentText like '%fixme%design%' or commentText like '% xxx %design%' or commentText like '%redesign%' "
				+ " or  commentText like '%todo%magic%' or commentText like '%fixme%magic%' or commentText like '%xxx%magic%' or  commentText like '%smell%';";
		Connection dataBaseConnection = ConnectionFactory.getSqlite();
		try{
			PreparedStatement preparedStatement = dataBaseConnection.prepareStatement(sql);
			preparedStatement.execute();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}