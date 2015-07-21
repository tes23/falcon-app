var styles = {
    'green': 'panel-success',
    'red': 'panel-danger'
};

function updateHeader(fromStyle, toStyle, text) {
    $('#panel-header').removeClass(fromStyle).addClass(toStyle);
    $('#panel-header').children(':first').text(text);
}

$(function(){
    // get websocket class, firefox has a different way to get it
    var WS = window['MozWebSocket'] ? window['MozWebSocket'] : WebSocket;

    var socket = new WS('@routes.UpdaterController.wsInterface().webSocketURL(request)');

    socket.onopen = function(e) {
        console.log("Connected");
        updateHeader(styles.red, styles.green, 'Connected')
    };

    socket.onmessage = function(event) {
        console.log("Message received: " + event.data);
        $('#socket-messages').append('<span>' + event.data + '</span><br/>');
    };

    socket.onclose = function() {
        console.log("Connection closed");
        updateHeader(styles.green, styles.red, 'Closed')
    };

    socket.onerror = function() {
        console.log("WebSocket error");
        updateHeader(styles.green, styles.red, 'Error')
    };

    //var socket = new io.Socket('localhost', {port: 9000});
    //var socket = new io.connect();
    //
    //socket.on('connect',function() {
    //    console.log('Client has connected to the server!');
    //});
    //
    //socket.on('message',function(data) {
    //    console.log('Received a message from the server!',data);
    //    $('#socket-messages').prepend('<p>'+ data +'</p>');
    //});
    //
    //socket.on('disconnect',function() {
    //    console.log('The client has disconnected!');
    //    alert('You are disconnected!');
    //});
});