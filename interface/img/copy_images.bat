@echo off
set "SRC=C:\Users\Lenovo\.gemini\antigravity\brain\d9c2b977-767e-48f2-a8a3-4925dc0fba7f"
set "DST=d:\WebJava\Repositories\demo\interface\img"

copy /Y "%SRC%\hero_travel_1773366179157.png" "%DST%\hero-bg.png" >nul 2>&1
copy /Y "%SRC%\tour_danang_1773366199513.png" "%DST%\tour-danang.png" >nul 2>&1
copy /Y "%SRC%\tour_phuquoc_1773366215928.png" "%DST%\tour-phuquoc.png" >nul 2>&1
copy /Y "%SRC%\tour_halong_1773366240191.png" "%DST%\tour-halong.png" >nul 2>&1
copy /Y "%SRC%\tour_sapa_1773366256985.png" "%DST%\tour-sapa.png" >nul 2>&1
copy /Y "%SRC%\tour_hoian_1773366276639.png" "%DST%\tour-hoian.png" >nul 2>&1
copy /Y "%SRC%\tour_nhatrang_1773366295519.png" "%DST%\tour-nhatrang.png" >nul 2>&1
rem Reuse previous session images for blogs
copy /Y "%SRC%\blog_1_1773364762114.png" "%DST%\blog-1.png" >nul 2>&1
copy /Y "%SRC%\blog_2_1773364779298.png" "%DST%\blog-2.png" >nul 2>&1
copy /Y "%SRC%\blog_3_1773364800251.png" "%DST%\blog-3.png" >nul 2>&1
echo DONE_COPYING
