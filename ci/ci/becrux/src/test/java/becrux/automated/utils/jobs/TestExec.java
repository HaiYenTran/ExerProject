//package becrux.automated.utils.jobs;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.nio.file.Files;
//
//import org.jvnet.hudson.test.JenkinsRule;
//
//import hudson.tasks.junit.JUnitResultArchiver;
//import hudson.model.Project;
//import hudson.tasks.Shell;
//
//public class TestExec extends JobJenkins{
//
//	private static final String nameOfJob="TESTEXEC";
//	private static final String pathToSuccess="src/test/java/becrux/workspace/succeeded";
//	private static final String pathToFail="src/test/java/becrux/workspace/failed";
//
//
//	public TestExec(JenkinsRule jenkins) {
//		super(jenkins, nameOfJob);
//	}
//
//	@Override
//	public void create() {
//		try {
//
//			job = jenkins.createFreeStyleProject(nameOfJob);
//			//JUNIT configuration
//			JUnitResultArchiver junit = new JUnitResultArchiver("**/*.xml");
//			junit.setKeepLongStdio(false);
//			junit.setHealthScaleFactor(1.0);
//			job.getPublishersList().add(junit);
//
//			//Copy script
//			Shell comand = new Shell("touch failed/summary-report.xml");
//			job.getBuildersList().add(comand);
//
//			job.save();
//			jenkins.waitUntilNoActivity();
//
//			setTests(false);
//			jenkins.waitUntilNoActivity();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void setShellTest(boolean status){
//		Shell comand = null;
//		if(status){
//			comand = new Shell("touch succeeded/summary-report.xml");
//		}
//		else{
//			comand = new Shell("touch failed/summary-report.xml");
//		}
//
//		try {
//			job.getBuildersList().removeAll(Shell.class);
//			job.getBuildersList().add(comand);
//			job.save();
//		} catch (IOException e) {
//			e.printStackTrace();
//		};
//	}
//
//
//	private void setTests(boolean status){
//		Project<?, ?> a = ((Project<?, ?>)jenkins.getInstance().getItem(nameOfJob));
//		File f = new File(a.getRootDir().getAbsolutePath(),"workspace");
//		try {
//			java.nio.file.Files.createDirectory(f.toPath());
//			a.setCustomWorkspace(f.getAbsolutePath());
//			if(status){
//				File f2 = new File(f.getAbsolutePath(),"succeeded");
//				java.nio.file.Files.createDirectory(f2.toPath());
//				copyFolder(new File(pathToSuccess), new File(a.getCustomWorkspace()+"/succeeded"));
//			}
//			else{
//				File f2 = new File(f.getAbsolutePath(),"failed");
//				java.nio.file.Files.createDirectory(f2.toPath());
//				copyFolder(new File(pathToFail), new File(a.getCustomWorkspace()+"/failed"));
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void removeTests(){
//		Project<?, ?> a = ((Project<?, ?>)jenkins.getInstance().getItem(nameOfJob));
//		File f = new File(a.getRootDir().getAbsolutePath(),"workspace");
//		try {
//			File f1 = new File(f.getAbsolutePath(),"succeeded");
//			Files.deleteIfExists(new File(f1.getAbsolutePath(),"summary-report.xml").toPath());
//			Files.deleteIfExists(f1.toPath());
//			File f2 = new File(f.getAbsolutePath(),"failed");
//			Files.deleteIfExists(new File(f2.getAbsolutePath(),"summary-report.xml").toPath());
//			Files.deleteIfExists(f2.toPath());
//			Files.deleteIfExists(f.toPath());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void setResultTest(boolean status){
//		setShellTest(status);
//		removeTests();
//		setTests(status);
//	}
//
//	private static void copyFolder(File src, File dest)
//	    	throws IOException{
//
//	    	if(src.isDirectory()){
//
//	    		//if directory not exists, create it
//	    		if(!dest.exists()){
//	    		   dest.mkdir();
//	    		   System.out.println("Directory copied from "
//	                              + src + "  to " + dest);
//	    		}
//
//	    		//list all the directory contents
//	    		String files[] = src.list();
//
//	    		for (String file : files) {
//	    		   //construct the src and dest file structure
//	    		   File srcFile = new File(src, file);
//	    		   File destFile = new File(dest, file);
//	    		   //recursive copy
//	    		   copyFolder(srcFile,destFile);
//	    		}
//
//	    	}else{
//	    		//if file, then copy it
//	    		//Use bytes stream to support all file types
//	    		InputStream in = new FileInputStream(src);
//	    	        OutputStream out = new FileOutputStream(dest);
//
//	    	        byte[] buffer = new byte[1024];
//
//	    	        int length;
//	    	        //copy the file content in bytes
//	    	        while ((length = in.read(buffer)) > 0){
//	    	    	   out.write(buffer, 0, length);
//	    	        }
//
//	    	        in.close();
//	    	        out.close();
//	    	        System.out.println("File copied from " + src + " to " + dest);
//	    	}
//	    }
//}
