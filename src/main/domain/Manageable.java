package main.domain;

import java.io.BufferedReader;
import java.io.IOException;

public interface Manageable {

	void read(BufferedReader bufferedReader) throws IOException;

	String getData();

	boolean equalsPassword(String userPw);

	boolean modifyPassword(String passwdFrom, String passwdTo);
	
}
