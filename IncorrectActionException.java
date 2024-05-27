package model;

public class IncorrectActionException extends Exception {
    String message = "";
    public IncorrectActionException(){
        super("Incorrect action format");
    }
    public IncorrectActionException(String m){
        super(m);
    }
}
