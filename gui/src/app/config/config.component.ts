import {AlertDefinitionParameter} from './aler-definition-params';
import {AlertConfigService} from './alert-config.service';
import {AlertDefinition} from './alert-definition';
import {Component, OnInit} from '@angular/core';
import {SelectItem} from 'primeng/api';

@Component({
  selector: 'app-config',
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.css']
})
export class ConfigComponent implements OnInit {

  displayDialog: boolean;
  definition: AlertDefinition = new AlertDefinition();
  selectedDefinition: AlertDefinition;
  newDefinition: boolean;
  definitions: AlertDefinition[];
  cols: any[];
  parameters: AlertDefinitionParameter[];
  effects: SelectItem[];
  signs: SelectItem[];

  constructor(private alertConfigService: AlertConfigService) {}

  ngOnInit() {
    this.cols = [
      {field: 'alertName', header: 'Name'},
      {field: 'location', header: 'Location'},
      {field: 'duration', header: 'Duration'},
      {field: 'active', header: 'Active'},
      {field: 'emailNotif', header: 'Email'},
      {field: 'slackNotif', header: 'Slack'},
      {field: 'timestamp', header: 'Timestamp'}
    ];
    this.effects = [
      {label: 'Rain', value: 'RAIN'},
      {label: 'Wind', value: 'WIND'},
      {label: 'Temperature', value: 'TEMP'},
      {label: 'Humidity', value: 'HUMI'}
    ];
    this.signs = [
      {label: '>', value: 1},
      {label: '<', value: 2}
    ];

    this.alertConfigService.getAlertDefinitions().subscribe(
      d => this.definitions = d,
      e => console.log(e));

  }

  showDialogToAdd() {
    this.newDefinition = true;
    this.definition = new AlertDefinition();
    this.displayDialog = true;
  }

  save() {
    // TODO first update database
    const defs = [...this.definitions];
    if (this.newDefinition) {
      defs.push(this.definition);
    } else {
      defs[this.definitions.indexOf(this.selectedDefinition)] = this.definition;
    }

    this.definitions = defs;
    this.definition = null;
    this.displayDialog = false;
  }

  delete() {
    const index = this.definitions.indexOf(this.selectedDefinition);
    this.definitions = this.definitions.filter((val, i) => i !== index);
    this.definition = null;
    this.displayDialog = false;
  }

  onRowSelect(event) {
    this.newDefinition = false;
    this.definition = this.cloneDefinition(event.data);
    this.displayDialog = true;
    this.parameters = this.definition.parameters;
  }

  cloneDefinition(d: AlertDefinition): AlertDefinition {
    let definition = new AlertDefinition;
    for (let prop in d) {
      definition[prop] = d[prop];
    }
    return definition;
  }

  public addParamRow() {
    const param = new AlertDefinitionParameter();
    param.parameterName = '';
    param.comparisonType = 1;
    param.parameterLimit = 0;

    this.parameters.push(param);
  }

}
