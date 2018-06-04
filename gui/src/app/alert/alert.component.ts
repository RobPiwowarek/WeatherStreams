import {UserService} from '../user/user.service';
import {Alert} from './alert';
import {AlertHistoryParam} from './alert-history-param';
import {AlertHistoryParamView} from './alert-history-param-view';
import {AlertService} from './alert-service';
import {Component, OnInit} from '@angular/core';
import { AbstractWalker } from 'tslint';

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
  alertHistoryParams: AlertHistoryParamView[];

  constructor(private alertService: AlertService, private userService: UserService) {}

  ngOnInit(): void {
    this.cols = [
      {field: 'name', header: 'Name'},
      {field: 'location', header: 'Location'},
      {field: 'date', header: 'Date'}
    ];

    this.username = this.userService.currentUser.name;

    this.alertService.getAlerts().subscribe(
      d => this.onAlertsLoad(d),
      e => console.log(e));
  }

  private onAlertsLoad(alerts) {
    this.alerts = alerts;
  }

  onRowSelect(event) {
    this.alert = this.selectedAlert;
    this.displayDialog = true;

    this.alertService.getAlertHitory(this.alert.id).subscribe(
      d => this.onHistoryLoad(d),
      e => console.log(e));
  }

  private onHistoryLoad(hist: AlertHistoryParam[]) {
    this.alertHistoryParams = hist.map(h => this.alertService.mapParamToParamView(h));
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
