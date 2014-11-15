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

	public void insertProcessed() {
		executeInsert(ConnectionFactory.getSqlite(), this.classCommentId, "INSERT INTO processed_comment (commentClassId, startLine, endLine, commentText, type, location, description) values(?,?,?,?,?,?,?)");
	}
	
	public void insert(Connection dataBaseConnection, long classCommentId) {
		executeInsert(dataBaseConnection, classCommentId, "INSERT INTO comment (commentClassId, startLine, endLine, commentText, type, location, description) values(?,?,?,?,?,?,?)");
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
		executeUpdate("UPDATE comment set commentClassId=?, startLine=?, endLine=?, commentText=?, type=?, location=?, description=? where id =?");
	}
	
	public void updateProcessed() {
		executeUpdate("UPDATE processed_comment set commentClassId=?, startLine=?, endLine=?, commentText=?, type=?, location=?, description=? where id =?");
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
			preparedStatement.setLong(8, this.id);
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
				comments.add(comment);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return comments;
	}
}