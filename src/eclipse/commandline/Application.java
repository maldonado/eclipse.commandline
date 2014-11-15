package eclipse.commandline;

import gr.uom.java.ast.ASTReader;
import gr.uom.java.ast.CompilationUnitCache;
import gr.uom.java.ast.Standalone;
import gr.uom.java.ast.SystemObject;
import gr.uom.java.distance.ExtractClassCandidateGroup;
import gr.uom.java.distance.ExtractClassCandidateRefactoring;
import gr.uom.java.distance.MoveMethodCandidateRefactoring;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSlice;
import gr.uom.java.jdeodorant.refactoring.manipulators.ASTSliceGroup;
import gr.uom.java.jdeodorant.refactoring.manipulators.TypeCheckElimination;
import gr.uom.java.jdeodorant.refactoring.manipulators.TypeCheckEliminationGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.PreferenceConstants;

import ca.evermal.comments.CommentProcessor;
import ca.evermal.comments.CommentsExtractor;

public class Application implements IApplication {

	@Override
	public Object start(IApplicationContext arg0) throws Exception {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IPath rootPath = root.getLocation();
		File rootFile = rootPath.toFile();
		File[] contents = rootFile.listFiles();
		
//		for(File file : contents) {
//			if(file.isDirectory() && !file.getName().startsWith(".")) {
//				String[] dirContents = file.list();
//				List<String> dirContentsList = Arrays.asList(dirContents);
//				if(dirContentsList.contains(".project")) {
//					IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(new Path(file.getPath() + "/.project"));
//					IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
//					if(!project.exists()) {
//						project.create(null);
//					}
//					else {
//						project.refreshLocal(IResource.DEPTH_INFINITE, null);
//					}
//					if (!project.isOpen()) {
//						project.open(null);
//					}
//					if(project.hasNature(JavaCore.NATURE_ID)) {
//						IJavaProject jproject = JavaCore.create(project);
//						if(!jproject.hasBuildState()) {
//							project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
//							System.out.println("Project " + project.getName() + " built");
//						}
//					}
//				}
//				else{}
//				{
//					boolean builtWithMaven = false;
//					File[] projectContents = file.listFiles();
//					for(File content : projectContents) {
//						if(content.isFile() && content.getName().equals("pom.xml")) {
//							M2RepoClasspathVariable(rootFile);
//							builtWithMaven = mvnEclipse(file);
//							if(builtWithMaven) {
//								System.out.println("Project " + file.getName() + " built with maven");
//							}
//							IProject project = root.getProject(file.getName());
//							if(!project.exists()) {
//								project.create(null);
//							}
//							else {
//								project.refreshLocal(IResource.DEPTH_INFINITE, null);
//							}
//							if (!project.isOpen()) {
//								project.open(null);
//							}
//							break;
//						}
//					}
//					
//					if(!builtWithMaven) {
//						//First create a simple project of type org.eclipse.core.resources.IProject:
//						IProject project = root.getProject(file.getName());
//						if(!project.exists()) {
//							project.create(null);
//						}
//						else {
//							project.refreshLocal(IResource.DEPTH_INFINITE, null);
//						}
//						if (!project.isOpen()) {
//							project.open(null);
//						}
//						//you can add in this list more names for source folders
//						List<String> sourceFolderNames = new ArrayList<String>();
//						sourceFolderNames.add("src");
//						sourceFolderNames.add("java");					
//						List<IResource> sourceFolderResources = new ArrayList<IResource>();
//						List<IResource> jarFileResources = new ArrayList<IResource>();
//						for(File content : projectContents) {
//							if(content.isDirectory()) {
//								IFolder folder = project.getFolder(content.getName());
//								jarFileResources.addAll(findFileResources(folder, "jar"));
//								sourceFolderResources.addAll(findFolderResources(folder, sourceFolderNames));
//							}
//						}
//						//remove the sourceFolders which have child sourceFolders
//						List<IResource> sourceFoldersToBeRemoved = new ArrayList<IResource>();
//						for(int i=0; i<sourceFolderResources.size(); i++) {
//							IResource resourceI = sourceFolderResources.get(i);
//							IPath pathI = resourceI.getFullPath();
//							for(int j=i+1; j<sourceFolderResources.size(); j++) {
//								IResource resourceJ = sourceFolderResources.get(j);
//								IPath pathJ = resourceJ.getFullPath();
//								if(pathI.isPrefixOf(pathJ)) {
//									sourceFoldersToBeRemoved.add(resourceI);
//								}
//							}
//						}
//						sourceFolderResources.removeAll(sourceFoldersToBeRemoved);
//						//Because we need a java project, we have to set the Java nature to the created project:
//						IProjectDescription description = project.getDescription();
//						description.setNatureIds(new String[] { JavaCore.NATURE_ID });
//						project.setDescription(description, null);
//
//						/*However, it's not enough if we want to add Java source code to the project. We have to set the Java build path:
//						(1) We first specify the output location of the compiler (the bin folder):*/
//						IFolder binFolder = project.getFolder("bin");
//						if(!binFolder.exists()) {
//							binFolder.create(false, true, null);
//						}
//						//Now we can create our Java project
//						IJavaProject javaProject = JavaCore.create(project);
//						javaProject.setOutputLocation(binFolder.getFullPath(), null);
//						
//						List<IClasspathEntry> libEntries = new ArrayList<IClasspathEntry>();
//						for(IResource resource : jarFileResources) {
//							IClasspathEntry libEntry = JavaCore.newLibraryEntry(resource.getFullPath(), null, null, false);
//							libEntries.add(libEntry);
//						}
//						List<IClasspathEntry> sourceEntries = new ArrayList<IClasspathEntry>();
//						for(IResource resource : sourceFolderResources) {
//							IPackageFragmentRoot packageFragmentRoot = javaProject.getPackageFragmentRoot(resource);
//							IClasspathEntry sourceEntry = JavaCore.newSourceEntry(packageFragmentRoot.getPath());
//							sourceEntries.add(sourceEntry);
//						}
//						List<IClasspathEntry> containerEntries = new ArrayList<IClasspathEntry>();
//						//add JUnit library
//						Path junitPath = new Path("org.eclipse.jdt.junit.JUNIT_CONTAINER/4");
//						IClasspathEntry junitEntry = JavaCore.newContainerEntry(junitPath);
//						containerEntries.add(JavaCore.newContainerEntry(junitEntry.getPath()));
//
//						IClasspathEntry[] defaultJRELibraryEntries = PreferenceConstants.getDefaultJRELibrary();
//						IClasspathEntry[] libEntryArray = libEntries.toArray(new IClasspathEntry[libEntries.size()]);
//						IClasspathEntry[] sourceEntryArray = sourceEntries.toArray(new IClasspathEntry[sourceEntries.size()]);
//						IClasspathEntry[] containerEntryArray = containerEntries.toArray(new IClasspathEntry[containerEntries.size()]);
//
//						IClasspathEntry[] newEntries = new IClasspathEntry[defaultJRELibraryEntries.length + libEntryArray.length +
//						                                                   sourceEntryArray.length + containerEntryArray.length];
//						System.arraycopy(defaultJRELibraryEntries, 0, newEntries, 0, defaultJRELibraryEntries.length);
//						System.arraycopy(libEntryArray, 0, newEntries, defaultJRELibraryEntries.length, libEntryArray.length);
//						System.arraycopy(sourceEntryArray, 0, newEntries,
//								defaultJRELibraryEntries.length + libEntryArray.length, sourceEntryArray.length);
//						System.arraycopy(containerEntryArray, 0, newEntries,
//								defaultJRELibraryEntries.length + libEntryArray.length + sourceEntryArray.length, containerEntryArray.length);
//						javaProject.setRawClasspath(newEntries, null);
//
//						if(!javaProject.hasBuildState()) {
//							project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
//							System.out.println("Project " + project.getName() + " built");
//						}
//					}
//				}
//			}
//		}
//
//		workspace.save(true, null);
		
//		processOpenJavaProjects(root);
		CommentProcessor processor = new CommentProcessor();
		processor.execute();
		return IApplication.EXIT_OK;
	}

	private void processOpenJavaProjects(IWorkspaceRoot root) throws CoreException {
		IProject[] projects = root.getProjects();
		for(IProject project : projects) {
			if(project.isOpen()) {
				System.out.println("Running JDeodorant on project " + project.getName());
				if(project.hasNature(JavaCore.NATURE_ID)) {
					IJavaProject jproject = JavaCore.create(project);
					CompilationUnitCache.getInstance().clearCache();
					if(ASTReader.getSystemObject() != null && project.equals(ASTReader.getExaminedProject())) {
						new ASTReader(jproject, ASTReader.getSystemObject(), null);
					}
					else {
						new ASTReader(jproject, null);
					}
					SystemObject systemObject = ASTReader.getSystemObject();
					CommentsExtractor.extractFrom(systemObject);
//					break;
//					List<MoveMethodCandidateRefactoring> moveMethodCandidateList = Standalone.getMoveMethodRefactoringOpportunities(jproject);
//					System.out.println("Move Method Refactoring Opportunities:");
//					for(MoveMethodCandidateRefactoring candidate : moveMethodCandidateList) {
//						System.out.println(candidate);
//					}
//
//					Set<TypeCheckEliminationGroup> typeCheckEliminationGroupList = Standalone.getTypeCheckEliminationRefactoringOpportunities(jproject);
//					System.out.println("Type Check Elimination Refactoring Opportunities:");
//					for(TypeCheckEliminationGroup group : typeCheckEliminationGroupList) {
//						List<TypeCheckElimination> typeCheckEliminationList = group.getCandidates();
//						for(TypeCheckElimination elimination : typeCheckEliminationList) {
//							System.out.println(elimination);
//						}
//					}
//
//					Set<ASTSliceGroup> sliceGroupList = Standalone.getExtractMethodRefactoringOpportunities(jproject);
//					System.out.println("Extract Method Refactoring Opportunities:");
//					for(ASTSliceGroup group : sliceGroupList) {
//						Set<ASTSlice> slices = group.getCandidates();
//						for(ASTSlice slice : slices) {
//							System.out.println(slice);
//						}
//					}
//
//					Set<ExtractClassCandidateGroup> extractClassGroupList = Standalone.getExtractClassRefactoringOpportunities(jproject);
//					System.out.println("Extract Class Refactoring Opportunities:");
//					for(ExtractClassCandidateGroup group : extractClassGroupList) {
//						List<ExtractClassCandidateRefactoring> candidates = group.getCandidates();
//						for(ExtractClassCandidateRefactoring candidate : candidates) {
//							System.out.println(candidate);
//						}
//					}
				}
			}
		}
//		CommentProcessor processor = new CommentProcessor();
//		processor.execute();
	}

	private List<IResource> findFileResources(IFolder resource, String extension) {
		List<IResource> resources = new ArrayList<IResource>();
		try {
			IResource[] members = resource.members();
			for(IResource childResource : members) {
				if(childResource.getType() == IResource.FOLDER) {
					IFolder folder = (IFolder)childResource;
					resources.addAll(findFileResources(folder, extension));
				}
				else if(childResource.getType() == IResource.FILE) {
					String ext = childResource.getFileExtension();
					if(ext != null && ext.equalsIgnoreCase(extension))
						resources.add(childResource);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return resources;
	}

	private List<IResource> findFolderResources(IFolder resource, List<String> folderNames) {
		List<IResource> resources = new ArrayList<IResource>();
		try {
			if(folderNames.contains(resource.getName())) {
				resources.add(resource);
			}
			IResource[] members = resource.members();
			for(IResource childResource : members) {
				if(childResource.getType() == IResource.FOLDER) {
					IFolder folder = (IFolder)childResource;
					resources.addAll(findFolderResources(folder, folderNames));
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return resources;
	}

	private boolean M2RepoClasspathVariable(File workspace) {
		boolean workspaceConfigured = false;
		try {
			Map<String, String> env = System.getenv();

			ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "mvn", "-Declipse.workspace=.", "eclipse:configure-workspace");
			Map<String, String> environment = pb.environment();
			for(String key : env.keySet()) {
				environment.put(key, env.get(key));
			}
			pb.directory(workspace);
			Process p = pb.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			String s = null;
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
				if(s.contains("BUILD SUCCESS"))
					workspaceConfigured = true;
			}
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return workspaceConfigured;
	}

	private boolean mvnEclipse(File directory) {
		boolean successfullyCompiled = false;
		try {
			Map<String, String> env = System.getenv();

			ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "mvn", "eclipse:eclipse");
			Map<String, String> environment = pb.environment();
			for(String key : env.keySet()) {
				environment.put(key, env.get(key));
			}
			pb.directory(directory);
			Process p = pb.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			String s = null;
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
				if(s.contains("BUILD SUCCESS"))
					successfullyCompiled = true;
			}
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return successfullyCompiled;
	}

	@Override
	public void stop() {

	}

}
