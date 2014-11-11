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
import java.util.HashSet;

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

	public void setText(String text) {
		this.text = text;
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


	public void insertProcessed() {
		Connection dataBaseConnection = ConnectionFactory.getSqlite();
		try {
			PreparedStatement preparedStatement = dataBaseConnection.prepareStatement("INSERT INTO processed_comment (commentClassId, startLine, endLine, commentText, type, location, description) values(?,?,?,?,?,?,?)");
			preparedStatement.setLong(1, this.classCommentId);
			preparedStatement.setInt(2, this.startLine);
			preparedStatement.setInt(3, this.endLine);
			preparedStatement.setString(4, this.text);
			preparedStatement.setString(5, this.type.toString());
			preparedStatement.setString(6, this.location);
			preparedStatement.setString(7, this.description);
			preparedStatement.execute();
			dataBaseConnection.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void insert(long classCommentId) {
		Connection dataBaseConnection = ConnectionFactory.getSqlite();
		try {
			PreparedStatement preparedStatement = dataBaseConnection.prepareStatement("INSERT INTO comment (commentClassId, startLine, endLine, commentText, type, location, description) values(?,?,?,?,?,?,?)");
			preparedStatement.setLong(1, classCommentId);
			preparedStatement.setInt(2, this.startLine);
			preparedStatement.setInt(3, this.endLine);
			preparedStatement.setString(4, this.text);
			preparedStatement.setString(5, this.type.toString());
			preparedStatement.setString(6, this.location);
			preparedStatement.setString(7, this.description);
			preparedStatement.execute();
			this.id = preparedStatement.getGeneratedKeys().getLong(1);
			dataBaseConnection.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	public static HashSet<Comment> findByCommentClassId(long classCommentId) {
		HashSet<Comment> comments = new HashSet<Comment>();
		Connection dataBaseConnection = ConnectionFactory.getSqlite();
		try{
			PreparedStatement preparedStatement = dataBaseConnection.prepareStatement("SELECT * FROM comment where commentClassId = ?");
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