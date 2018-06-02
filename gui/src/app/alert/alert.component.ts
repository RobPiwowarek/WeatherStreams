import {UserService} from '../user/user.service';
import {Alert} from './alert';
import {AlertHistoryParam} from './alert-history-param';
import {AlertService} from './alert-service';
import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent implements OnInit {

  username = 'Anonymous';
  displayDialog: boolean;
  cols: any[];
  alerts: Alert[];
  selectedAlert: Alert;
  alert: Alert;
  alertHistoryParams: AlertHistoryParam[];

  constructor(private alertService: AlertService, private userService: UserService) {}

  ngOnInit(): void {
    this.cols = [
      {field: 'name', header: 'Name'},
      {field: 'location', header: 'Location'},
      {field: 'date', header: 'Date'}
    ];

    this.username = this.userService.currentUser.name;

    this.alertService.getAlerts().subscribe(
      d => this.alerts = d,
      e => console.log(e));
  }

  onRowSelect(event) {
    this.alert = this.selectedAlert;
    this.displayDialog = true;

    this.alertService.getAlertHitory(this.alert.id).subscribe(
      d => this.alertHistoryParams = d,
      e => console.log(e));
  }


  delete() {
    const index = this.alerts.indexOf(this.selectedAlert);
    this.alerts = this.alerts.filter((val, i) => i !== index);

    if (this.selectedAlert.id) {
      this.alertService.deleteAlert(this.selectedAlert.id).subscribe(
        d => console.log('definition delete success'),
        e => console.log(e));
    }
    this.displayDialog = false;
    this.alert = null;
    this.alertHistoryParams = null;
  }

}
