import {UserService} from '../user/user.service';
import {Component, OnInit, Output, EventEmitter} from '@angular/core';

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent implements OnInit {
  msg = '';
  needLogin = true;
  @Output() userLogged: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.userService.logginSucceed.subscribe(
      result => this.processResult(result)
    );
  }

  processResult(result) {
    if (result) {
      this.needLogin = false;
      this.userLogged.emit(true);
    } else {
      this.msg = this.userService.errorMessage;
    }
  }

  login(username, password): void {
    this.userService.login(username, password);
  }

}
