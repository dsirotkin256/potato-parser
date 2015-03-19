package app;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

public class QuestionDoc extends LinkedList<Question> {

	
	HWPFDocument questionDoc;
	AnswerDoc answerDoc;
	
	WordExtractor questionsExt;
	
	private String docName;
	private String questionPath;
	
	
	public QuestionDoc(String path) {
		
		//System.out.println("Question path: "+path);
		
		String [] dirs = path.split(":?\\\\");
		
		String FileName = dirs[dirs.length-1];
		
		
		this.docName = FileName;
		this.questionPath = path;
		
		String AnswerFileName = FileName.replace(".doc", "").concat("_о").concat(".doc");
		
		Matcher m = Pattern.compile("((?:.+\\\\)+)").matcher(path);
		
		String answerPath = m.find()? m.group() + AnswerFileName : "";

		//System.out.println("Answer path: "+answerPath);
		
		try {
			this.questionDoc = new HWPFDocument(new FileInputStream(this.questionPath));
			this.answerDoc = new AnswerDoc(answerPath);
			this.questionsExt = new WordExtractor(questionDoc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		extractQuestions();
		
		
		
	}
	
	public String getDocText() {
		return this.questionsExt.getText();
	}
	
	public void setQuestionDoc(HWPFDocument questionDoc) {
		this.questionDoc = questionDoc;
	}
	
	public HWPFDocument getQuestionDoc() {
		return questionDoc;
	}
	
	public String getDocName() {
		return docName;
	}
	
	public String getDocPath() {
		return this.questionPath;
	}
	
	
	public void extractQuestions() {
		

		
		Pattern q = Pattern.compile("^(?<qn>\\d+)[ \\t]+"
				+ "(?<Link>[^ \\t]+ (?:\\d+ )?[^ \\t]+?)(?=[\u0410-\u042F] |[\u0410-\u042F][\u0430-\u044F])"
				+ "(?<q>.+)(r?\n|\r\n)"
				+ ".+?1[.][ \\t]+(?<a1>.+)(r?\n|\r\n)"
				+ ".+?2[.][ \\t]+(?<a2>.+)(r?\n|\r\n)"
				+ ".+?3[.][ \\t]+(?<a3>.+)(r?\n|\r\n)?"
				+ "(?:.+?4[.][ \\t]+(?<a4>.+))?$", Pattern.MULTILINE);
		
		Matcher m = q.matcher(this.getDocText());
				
		while (m.find()) {
			
			 add(new Question() {
				 
				 {
					 setDocName(docName);
					 
					 LinkedList <String> answers = new LinkedList<>();
					 
					 String qn = m.group("qn");

					 //System.out.println("Extracting question #"+qn);
					 
					 setQuestionNumber(qn);
					 
					 setLink(m.group("Link"));
					 
					 setQuestion(m.group("q"));
					 
					 answers.add(m.group("a1"));
					 answers.add(m.group("a2"));
					 answers.add(m.group("a3"));
					 answers.add(m.group("a4"));
					
					 
					 //System.out.print("Preparing to set correct answer...");
					 
					 setAnswer(extractValue(qn, answers));
					 
					 //System.out.println(getAnswer() + "\n");
					 
				 }
				 
			 });
			 
		}
		
	}
	
	
	Pattern getPattern(String questionNumber) {
		

		Pattern d = Pattern.compile("^(?<min>\\d+)-(?<max>\\d+)",Pattern.MULTILINE);
		
		Matcher dm = d.matcher(answerDoc.getDocText());
		
		String regex = null;
		
		while (dm.find()) {
			
			String minNumber = dm.group("min");
			String maxNumber = dm.group("max"); 
			
			
			if (questionNumber.length() == 1) {
				regex = "0-"+maxNumber;
				
				break;
				
			}
			
			else if (questionNumber.length() == 2) {
				
				if (questionNumber.charAt(0) == minNumber.charAt(0)) {
				
					regex = minNumber + "-" + maxNumber;
					break;
				}
			
			} else if (questionNumber.length() == 3) {
				
				int lastVal = Character.getNumericValue(questionNumber.charAt(2));
				
				if (maxNumber.length() == 3) {
				
					int lastValOfMax = Character.getNumericValue(maxNumber.charAt(2));
				
					if (questionNumber.charAt(0) == minNumber.charAt(0) 
							&& questionNumber.charAt(1) == minNumber.charAt(1) 
							&& lastVal <= lastValOfMax) {
						
						regex = minNumber + "-" + maxNumber;
						break;
						
					}
				}
			}
			
		
		}
		
		return Pattern.compile(regex + ".+(\n|\n\r)?.*",Pattern.MULTILINE);
	}
	
	
	
	String extractValue(String qNum, LinkedList<String> answers) {
		
		Pattern p = getPattern(qNum);
		
		
		Matcher m = p.matcher(answerDoc.getDocText());
		
		LinkedList <String> tableAnswers;
		int answer = 0;
		
		if(m.find()) {

			tableAnswers = new LinkedList<>(Arrays.asList(m.group().split("[\\t ]+")));
			tableAnswers.removeFirst();
			
			// get last number 
			int value = Character.getNumericValue(qNum.charAt(qNum.length() - 1));
			
			if (tableAnswers.size() == 9 && qNum.length() == 1) {
				value-=1;
			}
			
			// get value intable
			answer = Integer.parseInt(tableAnswers.get(value));
			
			
			return answers.get(answer - 1);
			
		}
		
		return null;
		
	}
	
	
}
