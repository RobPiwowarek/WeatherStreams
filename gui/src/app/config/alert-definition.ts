import {AlertDefinitionParameter} from './alert-definition-params';
export class AlertDefinition {
  id: number;
  userId: number;
  alertName: string;
  duration: number;
  location: string;
  active: boolean;
  emailNotif: boolean;
  slackNotif: boolean;
  timestamp: number;
  parameters: AlertDefinitionParameter[];
}
