package eclipse.commandline;

import java.sql.Connection;
import java.sql.PreparedStatement;

import gr.uom.java.ast.CommentObject;
import gr.uom.java.ast.CommentType;
import gr.uom.java.ast.ConstructorObject;
import gr.uom.java.ast.FieldObject;
import gr.uom.java.ast.MethodObject;

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


	public void setStartLine(int startLine) {
		this.startLine = startLine + LINE_CORRECTION;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine + LINE_CORRECTION;
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

		}catch(Exception e ){
			System.out.println(e);
		}
	}
}