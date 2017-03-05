package javarsaccounts;

import com.google.gson.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.SocketClient;
import com.DeathByCaptcha.Captcha;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

public class JavaRSAccounts {

    private final static String PASS_STRING = "Meder11";
    private final static String FILENAME_PNG = "test.png";
    private final static String FILENAME_JPG = "test.jpg";
    private final static String RUNESCAPE_URL = "https://secure.runescape.com/m=account-creation/g=oldscape/create_account?trialactive=true";
    private final static String RANDGEN_URL = "https://randomuser.me/api/?nat=gb";

    public static void main(String[] args) throws Exception {

        System.out.println("Enter number of accounts to make: ");
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        int accounts = sc.nextInt();
        System.out.println("Attempting to create " + accounts + " runescape accounts...");
        sleeping(1000);

        for (int x = 0; x < accounts; x++) {

            // Grab random user data from randomuser.me API

            String koordinate = "";
            String json = readUrl(RANDGEN_URL);
            JsonParser jsonParser = new JsonParser();
            JsonObject firstNameObject = jsonParser.parse(json)
                    .getAsJsonObject().getAsJsonArray("results").get(0)
                    .getAsJsonObject().getAsJsonObject("name");
            String firstNameString = firstNameObject.get("first").getAsString();

            JsonObject lastNameObject = jsonParser.parse(json)
                    .getAsJsonObject().getAsJsonArray("results").get(0)
                    .getAsJsonObject().getAsJsonObject("name");
            String lastNameString = lastNameObject.get("last").getAsString();

            Random randMail = new Random();
            int setMail = randMail.nextInt(90) + 10;
            String mail = firstNameString + "." + lastNameString + "" + setMail + "@gmail.com";

            JsonObject usernames = jsonParser.parse(json)
                    .getAsJsonObject().getAsJsonArray("results").get(0)
                    .getAsJsonObject().getAsJsonObject("login");
            String user = usernames.get("username").getAsString();

            if (user.length() > 12) {
                Random randNum = new Random();
                int setNum = randNum.nextInt(90) + 10;
                user = user.substring(0, Math.min(user.length(), 10)) + setNum;
            }

            FirefoxProfile fp = new FirefoxProfile();
            fp.setPreference("browser.privatebrowsing.autostart", "True");

            WebDriver driver = new FirefoxDriver(fp);
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

            driver.get(RUNESCAPE_URL);
            sleeping(1500);
            
            boolean exists;

            // Write account information into forms

            WebElement age = driver.findElement(By.cssSelector("input#age"));
            Random rand = new Random();
            int setAge = rand.nextInt(20) + 30;
            age.sendKeys(Integer.toString(setAge));
            System.out.println("Sending generated username: " + user);
            String useruppercase = Character.toUpperCase(user.charAt(0)) + user.substring(1);
            WebElement username = driver.findElement(By.cssSelector("input#charactername"));
            username.sendKeys(useruppercase);
            System.out.println("Sending generated email: " + mail);
            WebElement email = driver.findElement(By.cssSelector("input#email1"));
            email.sendKeys(mail);
            System.out.println("Sending passwords: " + PASS_STRING);
            WebElement password1 = driver.findElement(By.cssSelector("input#password1"));
            password1.sendKeys(PASS_STRING);
            WebElement password2 = driver.findElement(By.cssSelector("input#password2"));
            password2.sendKeys(PASS_STRING);
            
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
            exists = !driver.findElements(By.xpath("//iframe[@title=\"recaptcha widget\"]")).isEmpty();
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

            // Check if captcha box exists, otherwise click submit so it appears

            if(!exists) {
                System.out.println("Captcha box not found, clicking submit");
                WebElement submit = driver.findElement(By.cssSelector("input#submit"));
                submit.click();
            }

            sleeping(new Random().nextInt(1500) + 2000);
            List<WebElement> captchabox = driver.findElements(By.xpath("//iframe[@title=\"recaptcha widget\"]"));
            captchabox.get(0).click();
            System.out.println("Clicking on captcha iframe box");

            sleeping(new Random().nextInt(1500) + 2000);
           

            WebElement imagebox = driver.findElement(By.xpath("//iframe[@title=\"recaptcha challenge\"]"));

            // Screenshot captcha
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            BufferedImage fullImg = ImageIO.read(scrFile);
            Point point = imagebox.getLocation();
            int eleWidth = imagebox.getSize().getWidth();
            int eleHeight = imagebox.getSize().getHeight();
            // Crop image
            BufferedImage eleBufferedImage = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
            ImageIO.write(eleBufferedImage, "png", scrFile);

            FileUtils.copyFile(scrFile, new File(FILENAME_PNG));
            System.out.println("Saved screenshot as: " + FILENAME_PNG);

            // Convert image to JPG

            BufferedImage img = ImageIO.read(new File(FILENAME_PNG));
            BufferedImage imgBufferedImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            imgBufferedImage.createGraphics().drawImage(img, 0, 0, Color.BLACK, null);
            ImageIO.write(imgBufferedImage, "jpg", new File(FILENAME_JPG));
            System.out.println("Converting to jpg...");

            sleeping(new Random().nextInt(500) + 300);

            /////////////////////////////////////////
            ////////// START ////// DBC /////////////
            /////////////////////////////////////////

            // EXAMPLE RECAPTCHA_COORDINATES.
            // Put your DBC username & password here:
            //Client client = (Client)(new HttpClient(args[0], args[1]));
            String usernameDBC = "USER";
            String passwordDBC = "PASS";
            Client client = (Client) (new SocketClient(usernameDBC, passwordDBC));
            client.isVerbose = true;

            try {
                try {
                    System.out.println("Your balance is " + client.getBalance() + " US cents");
                } catch (IOException e) {
                    System.out.println("Failed fetching balance: " + e.toString());
                    return;
                }

                Captcha captcha = null;
                try {
                    // Upload a CAPTCHA and poll for its status with 120 seconds timeout.
                    // Put you CAPTCHA image file name, file object, input stream, or
                    // vector of bytes, and solving timeout (in seconds) if 0 the default value take place.
                    // please note we are specifying type=2 in the second argument 
                    captcha = client.decode(FILENAME_JPG, 2, 120);
                } catch (IOException e) {
                    System.out.println("Failed uploading CAPTCHA");
                    return;
                }
                if (null != captcha) {
                    koordinate = captcha.text;
                    System.out.println("CAPTCHA " + captcha.id + " solved: " + captcha.text);
                } else {
                    System.out.println("Failed solving CAPTCHA");
                }
            } catch (com.DeathByCaptcha.Exception e) {
                System.out.println(e);
            }

            /////////////////////////////////////////
            ////////// END //////// DBC /////////////
            /////////////////////////////////////////

            sleeping(2000);
            driver.switchTo().frame(imagebox);
            System.out.println("Switched to imagebox iframe");
            sleeping(2000);

            // Detect size and complete captcha

            boolean tableType44 = true, tableType42 = true;
            boolean tableType33 = !driver.findElements(By.xpath("//table[@class=\"rc-imageselect-table-33\"]")).isEmpty(); //126x126
            if (!tableType33) {
                tableType44 = !driver.findElements(By.xpath("//table[@class=\"rc-imageselect-table-44\"]")).isEmpty(); //95x95
            }
            if (!tableType44) {
                tableType42 = !driver.findElements(By.xpath("//table[@class=\"rc-imageselect-table-42\"]")).isEmpty(); //191x93 WxH
            }
            if (tableType33) {
                System.out.println("3x3 table detected");
                solve(koordinate, driver, 126.0f, 126.0f);
            } else if (tableType44) {
                System.out.println("4x4 table detected");
                solve(koordinate, driver, 95.0f, 95.0f);
            } else if (tableType42) {
                System.out.println("4x2 table detected");
                solve(koordinate, driver, 191.0f, 93.0f);
            }

            WebElement element = driver.findElement(By.xpath("//*[@id='recaptcha-verify-button']"));
            System.out.println("Confirming captcha...");
            sleeping(1000);
            Actions actions = new Actions(driver);
            actions.moveToElement(element).click().perform();

            driver.switchTo().defaultContent();
            System.out.println("Switched back to default");

            // Grab your internet IP with Amazon

            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in.readLine();
            System.out.println("My current IP: " + ip);

            System.out.println("Sending form submission...");
            WebElement submit = driver.findElement(By.cssSelector("input#submit"));
            submit.click();


            // Submit account data to your own web server via GET URL request.

            readUrl("https://WEBSITE.com/rsaccs/index.php?email=" + mail + "&geslo=" + PASS_STRING + "&user=" + user);
            System.out.println("Sending account data submission...");

            // Delete screenshot files after completed account creation

            File file1 = new File(FILENAME_PNG);
            File file2 = new File(FILENAME_JPG);
            file1.delete();
            file2.delete();

            sleeping(new Random().nextInt(500) + 1500);

            driver.quit();

            // If you are creating more than 1 account wait ~5 minutes before attempt.
            // Jagex blocks IP's after multiple accounts created withing a timeframe.

            if (accounts > 1) {
                int sleeper = new Random().nextInt(100000) + 300000; // 5 min
                System.out.println("Waiting " + Integer.toString(sleeper).substring(0, Integer.toString(sleeper).length() - 3) + " seconds before next account...");
                sleeping(sleeper);
            }
        }
    }

    private static void sleeping(int timer) {
        try {
            Thread.sleep(timer); //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static void solve(String koordinate, WebDriver driver, float offsetx, float offsety) {
        koordinate = koordinate.substring(1, koordinate.length() - 1);
        Pattern patt = Pattern.compile("\\[[0-9]+,[0-9]+\\]");
        Matcher match = patt.matcher(koordinate);
        while (match.find()) {
            String a = match.group();
            a = a.substring(1, a.length() - 1);

            int x_int = Integer.parseInt(a.split(",")[0]);
            int y_int = Integer.parseInt(a.split(",")[1]);

            int row = (int) Math.ceil(x_int / offsetx);
            int coll = (int) Math.ceil((y_int - 126) / offsety);
            System.out.println("Row: " + coll + " Coll: " + row);
            driver.findElement(By.xpath("//table/tbody/tr[" + coll + "]/td[" + row + "]")).click();
            sleeping(new Random().nextInt(250) + 250);
        }
    }

}
