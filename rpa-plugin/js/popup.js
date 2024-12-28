$(function () {
    //关闭录制
    chrome.storage.sync.set({openTranscribe: 'false'}, function() {
        console.log('关闭录制');
    });

    //开启录制
    $('#open_transcribe').click(e => {
        chrome.storage.sync.set({openTranscribe: 'true'}, function() {
            console.log('开启录制');
        });
    });
});




