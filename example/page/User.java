package example.page;

public class User {
	private String id;
	private String name;
	
	public User(int i, String string) {
		this.id = String.valueOf(i);
		this.name = string;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	

}
