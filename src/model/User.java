package model;

public abstract class User {
    protected String userId;
    protected String name;
    protected String email;
    protected String phone;
    protected String password; // 你 CSV 不一定有，先保留字段以对齐类图

    protected User(String userId, String name, String email, String phone, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }

    // Part1 里有 login/logout，可以保留为占位（不做认证也行）
    public boolean login(String inputPassword) {
        return password != null && password.equals(inputPassword);
    }

    public void logout() {
        // placeholder
    }
}
