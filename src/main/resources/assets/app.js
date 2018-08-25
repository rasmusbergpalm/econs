/*
{
	"GRAIN": {
		"count": 100,
		"logMeanEndowment": 2.0
	},
	"CATTLE": {
		"count": 100,
		"logMeanEndowment": 1.0
	},
	"WOOL": {
		"count": 100,
		"logMeanEndowment": 1.0
	},
	"COAL": {
		"count": 100,
		"logMeanEndowment": 1.0
	},
	"GOLD": {
		"count": 100,
		"logMeanEndowment": 1.0
	}
}
*/

document.getElementById("submit").onclick = function () {
    const data = {
        "GRAIN": parseFloat($("#grain").val()),
        "WOOL": parseFloat($("#wool").val()),
        "CATTLE": parseFloat($("#cattle").val()),
        "GOLD": parseFloat($("#gold").val()),
        "COAL": parseFloat($("#coal").val()),
    };

    console.log(data);
    $('#description').html("Trading...").show();
    $('#results').hide();
    //make call
    jQuery.ajax({
        url: "/trade",
        type: "POST",
        data: JSON.stringify(data),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            console.log(response);
            $('#description').hide();
            $('#results').show();

            const prices = response.prices;

            $('#prices td.grain').html(prices['GRAIN']);
            $('#prices td.cattle').html(prices['CATTLE']);
            $('#prices td.wool').html(prices['WOOL']);
            $('#prices td.coal').html(prices['COAL']);

            $('#agents').DataTable({
                destroy: true,
                data: response.agents,
                columns: [
                    {data: 'utility before'},
                    {data: 'GRAIN before'},
                    {data: 'CATTLE before'},
                    {data: 'WOOL before'},
                    {data: 'COAL before'},
                    {data: 'GOLD before'},

                    {data: 'utility after'},
                    {data: 'GRAIN after'},
                    {data: 'CATTLE after'},
                    {data: 'WOOL after'},
                    {data: 'COAL after'},
                    {data: 'GOLD after'},
                ]
            });
        }
    });
};
