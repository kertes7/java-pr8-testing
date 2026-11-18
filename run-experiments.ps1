# ПР8: серія запусків mvn test (правильний код → зламаний код → відновлення). Результати: outputs\mvn_*.txt
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot"
$mvn = "C:\Users\Kertes\tools\apache-maven-3.9.9\bin\mvn.cmd"
Set-Location $PSScriptRoot
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8"
[Console]::OutputEncoding = [Text.Encoding]::UTF8
$enc = New-Object System.Text.UTF8Encoding $false
New-Item -ItemType Directory -Force outputs | Out-Null
$sub = "src\main\java\ua\duikt\pr8\Subscription.java"
$svc = "src\main\java\ua\duikt\pr8\RenewalService.java"

function Run($name) {
  $o = & $mvn -B -ntp "-Dstyle.color=never" clean test 2>&1 | ForEach-Object { "$_" } |
       Where-Object { $_ -notmatch "^\[INFO\] (Download|Copying|Using|Nothing to compile|Changes detected|Compiling|--- |Building|Scanning|skip non|Reactor|-----)" -and $_.Trim() -ne "" }
  [IO.File]::WriteAllLines("$PWD\outputs\$name.txt", $o, $enc)
  "== $name =="; $o | Select-Object -Last 22
}
function Patch($file, $old, $new) {
  $t = [IO.File]::ReadAllText("$PWD\$file", $enc)
  if (-not $t.Contains($old)) { throw "pattern not found in $file : $old" }
  [IO.File]::WriteAllText("$PWD\$file", $t.Replace($old, $new), $enc)
}

# A) правильний код
Run "mvn_01_ok"

# B) навмисно зламано відсоток знижки: 0.10 -> 0.05
Patch $sub "DISCOUNT = 0.10" "DISCOUNT = 0.05"
Run "mvn_02_broken_discount"
Patch $sub "DISCOUNT = 0.05" "DISCOUNT = 0.10"

# C) зламано межу: >= 6 -> > 6
Patch $sub "if (months >= DISCOUNT_FROM_MONTHS)" "if (months > DISCOUNT_FROM_MONTHS)"
Run "mvn_03_broken_boundary"
$xml = [xml](Get-Content "target\surefire-reports\TEST-ua.duikt.pr8.SubscriptionTest.xml" -Encoding UTF8)
$failed = $xml.testsuite.testcase | Where-Object { $_.failure } | ForEach-Object { $_.name }
[IO.File]::WriteAllLines("$PWD\outputsailed_cases_boundary.txt", @("Тести, що впали при зламаній межі (>= 6 замінено на > 6):") + $failed, $enc)
"== failed cases (boundary) =="; $failed
Patch $sub "if (months > DISCOUNT_FROM_MONTHS)" "if (months >= DISCOUNT_FROM_MONTHS)"

# D) закоментовано виклик другої залежності (verify має впасти). Шаблони тільки ASCII.
Patch $svc "emailSender.send(email, String.format(Locale.US," "/* emailSender.send(email, String.format(Locale.US,"
Patch $svc "months, total));" "months, total)); */"
Run "mvn_04_mockito_missing_call"
Patch $svc "/* emailSender.send(email, String.format(Locale.US," "emailSender.send(email, String.format(Locale.US,"
Patch $svc "months, total)); */" "months, total));"

# E) контрольний запуск після відновлення
Run "mvn_05_restored_ok"
# F) (pom.xml вже пропатчено — цей крок виконується один раз) налаштування argLine: Mockito як Java-агент — попередження зникає
Patch "pom.xml" "</plugins>" "    <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <version>3.8.1</version>
                <executions>
                    <execution>
                        <id>mockito-agent-path</id>
                        <goals><goal>properties</goal></goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>"
Patch "pom.xml" "<version>3.5.2</version>" "<version>3.5.2</version>
                <configuration>
                    <argLine>-javaagent:`${org.mockito:mockito-core:jar}</argLine>
                </configuration>"
Run "mvn_06_argline_agent_ok"
"DONE"
