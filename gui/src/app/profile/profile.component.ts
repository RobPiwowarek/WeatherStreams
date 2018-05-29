import {User} from '../user/user';
import {UserService} from '../user/user.service';
import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  user: User;
  message: string;
  constructor(public router: Router, private userService: UserService) {}

  ngOnInit(): void {
    this.message = '';
    this.user = this.userService.currentUser;
  }

  public save() {
    this.message = '';
    this.userService.saveUser(this.user).subscribe(
      u => this.processSave(),
      e => this.processError(e)
    );
  }

  private processSave() {
    console.log('user update success');
    this.message = 'Settings updated.';
  }

  private processError(e) {
    console.log(e);
    this.message = e;
  }


}

