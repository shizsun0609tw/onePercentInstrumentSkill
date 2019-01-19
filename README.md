JAVA 期末專案第六組--OnePercentInstrumentSkill

期中提案投影片:
如附檔

期末報告投影片 hackmd link:
https://hackmd.io/xPDsdfzKRA-HJNgUj0_W7A?view

Github repo link:
https://github.com/shizsun0609tw/onePercentInstrumentSkill

1. 使用說明
	- 若是使用 .jar 直接執行, 則不得更改檔名, 並請至少保留5g ram
	- 若是使用 .jar 直接執行, 則需將.jar 與 ffmpeg/ 放在同個資料夾下
	- 若是使用 eclipse 執行專案, 則需在 eclipse 給定 JVM 參數, 開大記憶體
	- 若是使用 eclipse 執行專案, 請執行 OnePercentInstrumentSkill.java,
	並將 43-50 行註解(43-50行目的是在包裝成 .jar 時能夠重新開啟自己並加上
	JVM 的參數), 若未註解則會無法順利執行
2. 輸入說明
	- select midi: 選擇歌曲的midi檔
	- select folder: 選擇存放每個音的影片的資料夾
	- select background: 選擇背景圖片（optional）
	
3. 注意事項
	- 每個音的檔案名稱須按照以下規則命名:
		音名+是否升半音+音高
	音名為C, D, E, F, G, A, B 其中之一
	若是有升半音則加上"#", 沒有升的話就不用加,
	降半音的話則統一先降全音再升半音, 例如： Eb -> D#
	中央Ｃ(key == 60)為C4, 以此類推
	- 若需要測試用的 .mp4 data 可以從 GitHub 上的 src/test/resources/ 找到
	- ffmpeg/ 可在 GitHub 找到
4. 已知小問題
	- 確定能在 MacOS 10.14.1 與 Windows 10 執行
	- 在 MacOS 上執行完後會自動刪除 tmp 資料夾, 立意是希望能節省佔用的空間,
	但這會導致無法直接按第二次 run
	- 在 Windows 上執行完後原本應該會自動刪除 tmp 資料夾, 但好像因為路徑問題而不會刪掉,
	但反而因為沒有刪掉 tmp 所以可以在執行完第一次之後直接選 midi 檔跟 folder 直接 run 第二次
