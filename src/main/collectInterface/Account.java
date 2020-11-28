package main.collectInterface;

import java.util.Scanner;

public interface Account {

	void read(Scanner scanner);

	String getData();

	boolean equalsPassword(String userPw);

	boolean modifyPassword(String passwdFrom, String passwdTo);

	String getId();
}
