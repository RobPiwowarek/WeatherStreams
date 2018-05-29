import {UserService} from './user/user.service';
import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {MenuItem} from 'primeng/api';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  needLogin = true;
  username = 'anonim';
  menus: MenuItem[];

  userMenu = [
    {
      label: 'Active alerts',
      routerLink: ['/alert']
    },
    {
      label: 'Configuration',
      routerLink: ['/config']
    },
    {
      label: 'Profile',
      routerLink: ['/profile']
    }
  ];

  constructor(public router: Router, private userService: UserService) {}

  ngOnInit(): void {
  }

  onUserLogged(logged): void {
    this.needLogin = !logged;
    if (logged) {
      this.username = this.userService.getUsername();
      this.menus = this.userMenu;
      this.router.navigate(['/alert']);
    }
  }

  logout() {
    this.needLogin = true;
    this.userService.logout();
    this.router.navigate(['/']);
  }

}
