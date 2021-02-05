
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;

public class TestAppiumCrossPlatform {

    private AppiumDriver driver;
    String Platform = System.getProperty("platform");
    @Before
    public void setUp() throws Exception {

        if(Platform.equalsIgnoreCase("ios")){
            driver = createIosDriver();
        }else{
            driver = createAndroidDriver();
        }
    }

    @Test
    public void compareImages() throws Exception{
        Thread.sleep(1000);
        driver.get("https://office.live.com");
        Thread.sleep(2000);
        takeScreenShot(Platform);
        boolean comp = compareImages("/Users/sumanth_gundlakunta/ScreenShotAndroidbase.jpg","/Users/sumanth_gundlakunta/ScreenShotAndroid.jpg");
        assertTrue(comp);
    }

    @Test
    public void compareImages_Fail() throws Exception{
        Thread.sleep(1000);
        driver.get("https://office.live.com");
        Thread.sleep(2000);
        takeScreenShot(Platform);
        boolean comp = compareImages("/Users/sumanth_gundlakunta/ScreenShotAndroidbase1.jpg","/Users/sumanth_gundlakunta/ScreenShotAndroid.jpg");
        assertTrue(comp);
    }
    @Test
    public void runTestWordArabic() throws Exception{
        Thread.sleep(1000);
        driver.get("https://office.live.com/start/word.aspx");
        changeLanguage("pa-Arab-PK");
        signin();
        Thread.sleep(2000);
        findDocumentandClick("دستاویز1.docx");
        Thread.sleep(12000);
        searchForString("أصدقاء");
        validateResult();
    }

    @Test
    public void runTestWordChinese() throws Exception{
        Thread.sleep(1000);
        driver.get("https://office.live.com/start/word.aspx");
        changeLanguage("zh-CN");
        signin();
        Thread.sleep(2000);
        findDocumentandClick("文档.docx");
        Thread.sleep(10000);
        searchForString("盖茨和");
        validateResult();
    }

    private void signin() throws InterruptedException {
        Thread.sleep(5000);
        driver.findElement(By.name("loginfmt")).sendKeys("appiumtest@outlook.com");
        driver.findElement(By.name("passwd")).sendKeys("Test.1234");
        driver.findElement(By.name("SI")).submit();

    }

    private void changeLanguage(String language) throws InterruptedException{
        driver.navigate().to("https://office.live.com/start/Word.aspx?omkt="+language+"&auth=1");;

    }

    private void scrollToString(AndroidDriver driver,String element) throws InterruptedException{
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0,0)");
        do
        {
            try
            {

                WebElement Wl =driver.findElement(By.name(element));
                var ta = new TouchAction(driver);

                break;
            }
            catch(Exception e)
            {
                js = (JavascriptExecutor) driver;
                js.executeScript("window.scrollBy(0, 200)");
            }
        } while(true);
    }

    private void findDocumentandClick(String name){
        driver.findElement(By.partialLinkText(name)).click();
    }

    private void searchForString(String str){

        driver.context("NATIVE_APP");
        driver.findElement(By.id("com.android.chrome:id/document_menu_button")).click();

        driver.findElement(By.name("Find in page")).click();
        WebElement Wl = driver.findElement(By.id("com.android.chrome:id/find_query"));
        Wl.click();
        Wl.sendKeys(str);
    }

    private void validateResult(){
        String test1 = driver.findElement(By.id("com.android.chrome:id/find_status")).getText();
        String[] array = test1.split("/");
        assertEquals(1,Integer.parseInt(array[0]));
    }
    private boolean compareImages (String exp, String cur) throws IOException {
        BufferedImage img1 = ImageIO.read(new File(exp));
        BufferedImage img2 = ImageIO.read(new File(cur));
        int width1 = img1.getWidth(null);
        int width2 = img2.getWidth(null);
        int height1 = img1.getHeight(null);
        int height2 = img2.getHeight(null);
        if ((width1 != width2) || (height1 != height2)) {
            return false;
        }
        long diff = 0;
        for (int y = 0; y < height1; y++) {
            for (int x = 0; x < width1; x++) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >>  8) & 0xff;
                int b1 = (rgb1      ) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >>  8) & 0xff;
                int b2 = (rgb2      ) & 0xff;
                diff += Math.abs(r1 - r2);
                diff += Math.abs(g1 - g2);
                diff += Math.abs(b1 - b2);
            }
        }
        double n = width1 * height1 * 3;
        double p = diff / n / 255.0;
        if(Double.compare(p, 0.0) == 0){
            return true;
        }
        return false;

    }

    private void takeScreenShot(String Platform) throws IOException {

        File newFile = driver.getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(newFile, new File("/Users/sumanth_gundlakunta/"+"ScreenShot"+Platform+".jpg"));
    }

    @SuppressWarnings("rawtypes")
    private IOSDriver createIosDriver() throws  Exception{
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", "iPhone 6");
        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("platformVersion", "8.2");
        capabilities.setCapability("browserName", "safari");
        //capabilities.setCapability("udid", "a00e23be8f73c2e3f7de08d4ea91ef200f26552c");
        driver = new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"),
                capabilities);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        return (IOSDriver) driver;
    }

    @SuppressWarnings("rawtypes")
    private AndroidDriver createAndroidDriver() throws MalformedURLException{
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME,"Android");
        capabilities.setCapability(MobileCapabilityType.BROWSER_NAME,"Chrome");
        capabilities.setCapability("newCommandTimeout", "300");
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,"Galaxy Note4");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "5.0.1");
        capabilities.setCapability("unicodeKeyboard", true);
        capabilities.setCapability("resetKeyboard", true);
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"),
                capabilities);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        return (AndroidDriver) driver;
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }
}
