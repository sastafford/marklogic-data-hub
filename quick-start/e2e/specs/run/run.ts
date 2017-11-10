import { protractor , browser, element, by, By, $, $$, ExpectedConditions as EC} from 'protractor';
import { pages } from '../../page-objects/page';
import loginPage from '../../page-objects/auth/login';
import dashboardPage from '../../page-objects/dashboard/dashboard';
import entityPage from '../../page-objects/entities/entities';
import flowPage from '../../page-objects/flows/flows';

export default function() {
  describe('Run Flows', () => {
    beforeAll(() => {
      flowPage.isLoaded();
    });

    it ('should redeploy modules', function() {
      flowPage.redeployButton.click();
      // let message = 'Redeploying Modules...';
      // browser.wait(EC.visibilityOf(flowPage.toast));
      // expect(flowPage.toast.isDisplayed()).toBe(true);
      browser.wait(EC.visibilityOf(element(by.cssContainingText('#last-deployed-time', 'Last Deployed: less than a minute ago'))));
    });

    let flowCount = 1;
    ['sjs', 'xqy'].forEach((codeFormat) => {
      ['xml', 'json'].forEach((dataFormat) => {
        let flowType = 'INPUT';
        let flowName = `${codeFormat} ${dataFormat} ${flowType}`;
        it (`should run a ${flowName} flow`, function() {
          flowPage.runInputFlow('TestEntity', flowName, dataFormat, flowCount);
          flowCount++;
        });
      });
    });
  });
}
