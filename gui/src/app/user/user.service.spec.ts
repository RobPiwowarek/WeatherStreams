import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {UserService} from './user.service';
import {User} from './user';
//import { defer } from 'rxjs';

describe('UserService', () => {
  let httpClientSpy: {get: jasmine.Spy};
  let userService: UserService;

  beforeEach(() => {
    // TODO: spy on other methods too
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['post', 'get']);
    userService = new UserService(<any>httpClientSpy);
  });


  it('should login anonyous (HttpClient not called)', () => {
    userService.login(null, null);

    expect(httpClientSpy.get.calls.count()).toBe(0, 'no call');
    expect(userService.currentUser.name).toBe('Anonymous', 'Anonymous user login')
  });


//  it('should login  (HttpClient once called)', () => {
//    const user: User = {
//        id: 1,
//        username: 'Test@test.com',
//        name: 'Test',
//        surname: 'Some',
//        slack: 'myslack'
//      };
//
//    httpClientSpy.get.and.returnValue(defer(() => Promise.resolve(user)));
//
//    userService.login(null, null);
//
//    expect(httpClientSpy.get.calls.count()).toBe(1, 'once call');
//    expect(userService.currentUser.name).toBe('Test', 'Test user login')
//  });



});

