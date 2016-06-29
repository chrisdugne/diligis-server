package domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Manager
{
	protected static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
}