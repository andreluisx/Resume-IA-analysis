package dev.andre.ResumeAiAnalysis.User.Exceptions;

public class EmailOrPasswordInvalid extends RuntimeException{

    public EmailOrPasswordInvalid(String message){
        super(message);
    }
}
