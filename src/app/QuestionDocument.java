package app;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionDocument extends Document implements Comparable<QuestionDocument> {

    private AnswerDocument answerDocument;
    private TreeSet<Question> questions;

    public QuestionDocument(String path) throws IOException {
        super(path);
        questions = new TreeSet<>();
    }

    public TreeSet<Question> getQuestions() {
        return questions;
    }

    public void setAnswerDocument(String path) throws IOException {
        this.answerDocument = new AnswerDocument(path);
    }

    public void extractQuestions() {
        questions.clear();

        String regex = "^(?<qn>\\d+)[ \\t]+"
                + "(?<Link>[^ \\t]+ (?:\\d+ )?[^ \\t]+?)(?=[\u0410-\u042F] |[\u0410-\u042F][\u0430-\u044F])"
                + "(?<q>.+)(r?\n|\r\n)"
                + ".+?1[.][ \\t]+(?<a1>.+)(r?\n|\r\n)"
                + ".+?2[.][ \\t]+(?<a2>.+)(r?\n|\r\n)"
                + ".+?3[.][ \\t]+(?<a3>.+)(r?\n|\r\n)?"
                + "(?:.+?4[.][ \\t]+(?<a4>.+))?$";

        Pattern q = Pattern.compile(regex, Pattern.MULTILINE);

        Matcher m = q.matcher(this.toString());

        while (m.find()) {

            questions.add(new Question() {
                {
                    setDocName(name.replace(".doc", ""));
                    String number = m.group("qn");
                    setQuestionNumber(number);
                    setQuestion(m.group("q"));
                    String[] answers = {m.group("a1"), m.group("a2"), m.group("a3"), m.group("a4")};
                    setAnswer(extractValue(number, answers));
                    setLink(m.group("Link"));
                }
            });

        }

    }

    Pattern getPattern(String questionNumber) {

        String regexPattern = "^(?<min>\\d+)-(?<max>\\d+)";

        Pattern d = Pattern.compile(regexPattern, Pattern.MULTILINE);

        Matcher dm = d.matcher(answerDocument.toString());

        String regex = null;

        while (dm.find()) {

            String minNumber = dm.group("min");
            String maxNumber = dm.group("max");

            if (questionNumber.length() == 1) {
                regex = "0-" + maxNumber;

                break;

            } else if (questionNumber.length() == 2) {

                if (questionNumber.charAt(0) == minNumber.charAt(0)) {

                    regex = minNumber + "-" + maxNumber;
                    break;
                }

            } else if (questionNumber.length() == 3) {

                int lastVal = Character.getNumericValue(questionNumber.charAt(2));

                if (maxNumber.length() == 3) {

                    int lastValOfMax = Character.getNumericValue(maxNumber.charAt(2));

                    boolean is3 = questionNumber.charAt(0) == minNumber.charAt(0)
                            && questionNumber.charAt(1) == minNumber.charAt(1)
                            && lastVal <= lastValOfMax;

                    if (is3) {

                        regex = minNumber + "-" + maxNumber;
                        break;

                    }
                }
            }
        }

        regex += ".+(\n|\n\r)?.*";

        return Pattern.compile(regex, Pattern.MULTILINE);

    }

    String extractValue(String qNum, String[] answers) {

        Pattern p = getPattern(qNum);

        Matcher m = p.matcher(answerDocument.toString());

        LinkedList<String> numericAnswers;
        int answer;

        if (m.find()) {

            numericAnswers = new LinkedList<>(Arrays.asList(m.group().split("[\\t ]+")));
            numericAnswers.removeFirst();

            // get last number
            int value = Character.getNumericValue(qNum.charAt(qNum.length() - 1));

            if (numericAnswers.size() == 9 && qNum.length() == 1) {
                value -= 1;
            }

            // get value intable
            answer = Integer.parseInt(numericAnswers.get(value));

            return answers[answer - 1];

        }

        return null;

    }

    @Override
    public int compareTo(QuestionDocument doc) {

        return getName().compareTo(doc.getName());

    }

}
