export class UserRegisterModel {
    constructor(fullName, email, password, role = 'USER') {
      this.fullName = fullName;
      this.email = email;
      this.password = password;
      this.role = role;
    }
  }
  