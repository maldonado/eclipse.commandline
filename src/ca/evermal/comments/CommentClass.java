package ca.evermal.comments;

import gr.uom.java.ast.ASTReader;
import gr.uom.java.ast.Access;
import gr.uom.java.ast.ClassObject;
import gr.uom.java.ast.CommentObject;
import gr.uom.java.ast.ConstructorObject;
import gr.uom.java.ast.FieldObject;
import gr.uom.java.ast.MethodObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ListIterator;

import org.eclipse.jdt.core.IJavaProject;

public class CommentClass {


	private static final int LINE_CORRECTION = 1;
	
	private long id;
	private boolean _abstract;
	private boolean _enum;
	private boolean _interface;
	private Access access;
	private String projectName;
	private String fileName;
	private String className;
	private ArrayList<Comment> commentList;
	private int startLine;
	private int endLine;
	private int analyzed;
	private Connection dataBaseConnection;

	public CommentClass(){};
	
	public CommentClass(ClassObject classObject, Connection connection){
		
		dataBaseConnection = connection;
		
		IJavaProject examinedProject = ASTReader.getExaminedProject();
		this.projectName = examinedProject.getElementName();

		this._abstract = classObject.isAbstract();
		this._interface = classObject.isInterface();
		this._enum = classObject.isEnum();
		this.access = classObject.getAccess();
		this.fileName = classObject.getIFile().getName();
		this.className = classObject.getName();
		this.startLine =  classObject.getStartLine();
		this.endLine = classObject.getEndLine();
		this.commentList = setCommentList(classObject.getCommentIterator(), classObject.getFieldIterator(), classObject.getMethodIterator(), classObject.getConstructorIterator());
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public void set_abstract(boolean _abstract) {
		this._abstract = _abstract;
	}

	public void set_enum(boolean _enum) {
		this._enum = _enum;
	}

	public void set_interface(boolean _interface) {
		this._interface = _interface;
	}

	public void setAccess(Access access) {
		this.access = access;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getClassName() {
		return className;
	}

	public void setCommentList(ArrayList<Comment> commentList) {
		this.commentList = commentList;
	}

	public void setAnalyzed(int value){
		this.analyzed = value;
	}
	
	public int getAnalyzed(){
		return this.analyzed;
	}
	
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public long getId() {
		return id;
	}

	public ArrayList<Comment> getCommentList() {
		return commentList;
	}
	
	public int getStartLine() {
		return startLine;
	}

	public String getProjectName() {
		return projectName;
	}

	private ArrayList<Comment> setCommentList(ListIterator<CommentObject> allComments, ListIterator<FieldObject> allFields, ListIterator<MethodObject> allMethods, ListIterator<ConstructorObject> allConstructors) {
		ArrayList<Comment> commentList = new ArrayList<Comment>();
		populateFieldComments(allComments, allFields, commentList);
		populateConstructorComments(allComments, allConstructors, commentList);
		populateMethodsCommients(allComments, allMethods, commentList);
		populateClassComments(allComments, commentList);
		return commentList;
	}

	private void populateClassComments(ListIterator<CommentObject> allComments, ArrayList<Comment> commentList) {
		while(allComments.hasNext()){
			Comment comment = new Comment(this.className, allComments.next());
			commentList.add(comment);
		}
	}

	private void populateMethodsCommients(ListIterator<CommentObject> allComments,
			ListIterator<MethodObject> allMethods, ArrayList<Comment> commentList) {
		while(allMethods.hasNext()){
			MethodObject method = allMethods.next();
			ListIterator<CommentObject> methodComments = method.getCommentListIterator();
			while(methodComments.hasNext()){
				CommentObject methodCommentObject = methodComments.next();
				Comment comment = new Comment(method, methodCommentObject);
				commentList.add(comment);
				while(allComments.hasNext()){
					CommentObject classCommentObject = allComments.next();
					if(classCommentObject.equals(methodCommentObject))
						allComments.remove();
				}
				while(allComments.hasPrevious()){
					allComments.previous();
				}
			}
		}
	}

	private void populateConstructorComments(ListIterator<CommentObject> allComments,
			ListIterator<ConstructorObject> allConstructors, ArrayList<Comment> commentList) {
		while(allConstructors.hasNext()){
			ConstructorObject constructor = allConstructors.next();
			ListIterator<CommentObject> constructorComments = constructor.getCommentListIterator();
			while(constructorComments.hasNext()){
				CommentObject fieldCommentObject = constructorComments.next();
				Comment comment = new Comment(constructor, fieldCommentObject);
				commentList.add(comment);
				while(allComments.hasNext()){
					CommentObject classCommentObject = allComments.next();
					if(classCommentObject.equals(fieldCommentObject))
						allComments.remove();
				}
				while(allComments.hasPrevious()){
					allComments.previous();
				}
			}
		}
	}

	private void populateFieldComments(ListIterator<CommentObject> allComments, ListIterator<FieldObject> allFields,
			ArrayList<Comment> commentList) {
		while(allFields.hasNext()){
			FieldObject field = allFields.next();
			ListIterator<CommentObject> fieldComments = field.getCommentListIterator();
			while(fieldComments.hasNext()){
				CommentObject fieldCommentObject = fieldComments.next();
				Comment comment = new Comment(field, fieldCommentObject);
				commentList.add(comment);
				while(allComments.hasNext()){
					CommentObject classCommentObject = allComments.next();
					if(classCommentObject.equals(fieldCommentObject))
						allComments.remove();
				}
				while(allComments.hasPrevious()){
					allComments.previous();
				}
			}
		}
	}

	public void insert() {
		try {

			PreparedStatement preparedStatement = dataBaseConnection.prepareStatement("INSERT INTO comment_class (projectName, "
					+ "fileName, className, access, isAbstract, isEnum, isInterface, startLine, endLine, analyzed) values (?,?,?,?,?,?,?,?,?,?) returning id");

			preparedStatement.setString(1, this.projectName);
			preparedStatement.setString(2, this.fileName);
			preparedStatement.setString(3, this.className);
			preparedStatement.setString(4, this.access.toString());
			preparedStatement.setString(5, _abstract ? "true" : "false");
			preparedStatement.setString(6, _enum ? "true" : "false");
			preparedStatement.setString(7, _interface ? "true" : "false");
			preparedStatement.setInt(8, startLine + LINE_CORRECTION);
			preparedStatement.setInt(9, endLine + LINE_CORRECTION);
			preparedStatement.setInt(10, this.analyzed);
			preparedStatement.execute();
//			postgresql
			ResultSet resultSet = preparedStatement.getResultSet();
			while(resultSet.next()){
				this.id = resultSet.getLong("id");
			}
//			sqlite
//			this.id = preparedStatement.getGeneratedKeys().getLong(1);
			
			for (Comment comment : commentList) {
				comment.insert(dataBaseConnection, this.id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void update(){
		try {
			PreparedStatement preparedStatement = dataBaseConnection.prepareStatement("UPDATE comment_class set projectName = ?, fileName =?, "
					+ "className = ?, access=? , isAbstract =?, isEnum =?, isInterface =?, startLine =?, endLine=?, analyzed=? where id =?");

			preparedStatement.setString(1, this.projectName);
			preparedStatement.setString(2, this.fileName);
			preparedStatement.setString(3, this.className);
			preparedStatement.setString(4, this.access.toString());
			preparedStatement.setString(5, _abstract ? "true" : "false");
			preparedStatement.setString(6, _enum ? "true" : "false");
			preparedStatement.setString(7, _interface ? "true" : "false");
			preparedStatement.setInt(8, this.startLine);
			preparedStatement.setInt(9, this.endLine);
			preparedStatement.setInt(10, this.analyzed);
			preparedStatement.setLong(11, this.id);
			preparedStatement.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
	
	public static ArrayList<CommentClass> getAll(Connection connection, String projectName) {
		System.out.println("Loading inserted comment_classes");
		Connection dataBaseConnection = connection;
		ArrayList<CommentClass> result = new ArrayList<CommentClass>();
		try{
			PreparedStatement preparedStatement = dataBaseConnection.prepareStatement("SELECT * FROM comment_class where projectName=?");
			preparedStatement.setString(1, projectName);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				CommentClass commentClass = new CommentClass();
				commentClass.setId(resultSet.getLong("id"));
				commentClass.set_abstract(resultSet.getString("isAbstract").equals("true") ? true : false);
				commentClass.set_enum(resultSet.getString("isEnum").equals("true") ? true : false);
				commentClass.set_interface(resultSet.getString("isInterface").equals("true") ? true : false);
				String accessFromDB = resultSet.getString("access").toUpperCase().equals("") ? "NONE" : resultSet.getString("access").toUpperCase();
				commentClass.setAccess(Access.valueOf(accessFromDB));
				commentClass.setProjectName(resultSet.getString("projectName"));
				commentClass.setFileName(resultSet.getString("fileName"));
				commentClass.setClassName(resultSet.getString("className"));
				commentClass.setStartLine(resultSet.getInt("startLine")); 
				commentClass.setEndLine(resultSet.getInt("endLine")); 
				commentClass.setAnalyzed(resultSet.getInt("analyzed"));
				commentClass.setCommentList(Comment.findByCommentClassId(dataBaseConnection, commentClass.getId()));
				result.add(commentClass);
			}
			System.out.println("comment_classes loaded for " + projectName);
			return result;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static ArrayList<CommentClass> getAllThatHasProcessedComments(Connection connection, String projectName) {
		System.out.println("Loading inserted comment_classes");
		Connection dataBaseConnection = connection;
		ArrayList<CommentClass> result = new ArrayList<CommentClass>();
		try{
			PreparedStatement preparedStatement = dataBaseConnection.prepareStatement("SELECT a.* FROM comment_class a, processed_comment b where a.id = b.commentClassId and a.projectName=?");
			preparedStatement.setString(1, projectName);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				CommentClass commentClass = new CommentClass();
				commentClass.setId(resultSet.getLong("id"));
				commentClass.set_abstract(resultSet.getString("isAbstract").equals("true") ? true : false);
				commentClass.set_enum(resultSet.getString("isEnum").equals("true") ? true : false);
				commentClass.set_interface(resultSet.getString("isInterface").equals("true") ? true : false);
				String accessFromDB = resultSet.getString("access").toUpperCase().equals("") ? "NONE" : resultSet.getString("access").toUpperCase();
				commentClass.setAccess(Access.valueOf(accessFromDB));
				commentClass.setProjectName(resultSet.getString("projectName"));
				commentClass.setFileName(resultSet.getString("fileName"));
				commentClass.setClassName(resultSet.getString("className"));
				commentClass.setStartLine(resultSet.getInt("startLine")); 
				commentClass.setEndLine(resultSet.getInt("endLine")); 
				commentClass.setAnalyzed(resultSet.getInt("analyzed"));
				result.add(commentClass);
			}
			System.out.println("comment_classes loaded...");
			return result;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return result;
	}

	public static ArrayList<CommentClass> getDictionaryMatchedByProject(String projectName, Connection connection) {
		System.out.println("Loading inserted comment_classes");
		Connection dataBaseConnection = connection;
		ArrayList<CommentClass> result = new ArrayList<CommentClass>();
		try{
			PreparedStatement preparedStatement = dataBaseConnection.prepareStatement("SELECT a.id, a.projectName, a.fileName, a.className,"
					+ "a.access, a.isAbstract, a.isEnum, a.isInterface, a.startLine, a.endLine, a.analyzed FROM comment_class a, "
					+ "processed_comment b where a.id = b.commentClassId and b.dictionary_hit = 1 and a.projectName=? and a.analyzed = 0 group by a.id ");
			preparedStatement.setString(1, projectName);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				CommentClass commentClass = new CommentClass();
				commentClass.setId(resultSet.getLong("id"));
				commentClass.set_abstract(resultSet.getString("isAbstract").equals("true") ? true : false);
				commentClass.set_enum(resultSet.getString("isEnum").equals("true") ? true : false);
				commentClass.set_interface(resultSet.getString("isInterface").equals("true") ? true : false);
				String accessFromDB = resultSet.getString("access").toUpperCase().equals("") ? "NONE" : resultSet.getString("access").toUpperCase();
				commentClass.setAccess(Access.valueOf(accessFromDB));
				commentClass.setProjectName(resultSet.getString("projectName"));
				commentClass.setFileName(resultSet.getString("fileName"));
				commentClass.setClassName(resultSet.getString("className"));
				commentClass.setStartLine(resultSet.getInt("startLine")); 
				commentClass.setEndLine(resultSet.getInt("endLine")); 
				commentClass.setAnalyzed(resultSet.getInt("analyzed"));
				commentClass.setCommentList(Comment.findMatchedbyCommentClassId(dataBaseConnection, commentClass.getId()));
				result.add(commentClass);
			}
			System.out.println("comment_classes loaded...");
			return result;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static ArrayList<String> getNotUniqueClassFileName(Connection connection, String projectName){
		System.out.println("Loading not unique class file name");
		ArrayList<String> result = new ArrayList<String>();
		try{
			PreparedStatement preparedStatement = connection.prepareStatement("select filename, count(*)  from comment_class where projectName = ? group by 1 having count(*) > 1");
			preparedStatement.setString(1, projectName);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				String fileName = resultSet.getString("filename");
				result.add(fileName);
			}
			System.out.println("files names loaded...");
			return result;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return result;
	}

	public static ArrayList<CommentClass> getAllWithSameFileName(Connection connection, String fileName) {
		ArrayList<CommentClass> result = new ArrayList<CommentClass>();
		try{
			PreparedStatement preparedStatement = connection.prepareStatement("select * from comment_class where filename = ? order by id;");
			preparedStatement.setString(1, fileName);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				CommentClass commentClass = new CommentClass();
				commentClass.setId(resultSet.getLong("id"));
				commentClass.set_abstract(resultSet.getString("isAbstract").equals("true") ? true : false);
				commentClass.set_enum(resultSet.getString("isEnum").equals("true") ? true : false);
				commentClass.set_interface(resultSet.getString("isInterface").equals("true") ? true : false);
				String accessFromDB = resultSet.getString("access").toUpperCase().equals("") ? "NONE" : resultSet.getString("access").toUpperCase();
				commentClass.setAccess(Access.valueOf(accessFromDB));
				commentClass.setProjectName(resultSet.getString("projectName"));
				commentClass.setFileName(resultSet.getString("fileName"));
				commentClass.setClassName(resultSet.getString("className"));
				commentClass.setStartLine(resultSet.getInt("startLine")); 
				commentClass.setEndLine(resultSet.getInt("endLine")); 
				commentClass.setAnalyzed(resultSet.getInt("analyzed"));
				result.add(commentClass);
			}
			return result;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return result;
	}
}