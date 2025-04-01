let emailTimerInterval;

function startServerBasedCountdown(email) {
    clearInterval(emailTimerInterval);
    const $timerDisplay = $("#email-timer");

    emailTimerInterval = setInterval(() => {
        fetch(`/signup/email/ttl?email=${encodeURIComponent(email)}`)
            .then(response => response.json())
            .then(seconds => {
                if (seconds <= 0) {
                    clearInterval(emailTimerInterval);
                    alert("인증 시간이 만료되었습니다. 다시 인증번호를 받아주세요.");
                    $("#email-timer").text("");
                    $("#emailAuth").prop("disabled", true);
                    $("#emailAuth-button").prop("disabled", true);
                } else {
                    const minutes = Math.floor(seconds / 60);
                    const secs = seconds % 60;
                    $timerDisplay.text(
                        `남은 시간: ${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
                    );
                }
            })
            .catch(error => {
                console.error('타이머 조회 실패:', error);
                clearInterval(emailTimerInterval);
            });
    }, 1000);
}

$(document).ready(function () {
    $("#email-button").click(function () {
        var email = $("#email").val().trim();
        if (email === "") {
            alert("이메일을 입력하세요.");
            return;
        }
        var mailRequestDto = { email: email };
        $.ajax({
            type: "POST",
            url: "/signup/email",
            contentType: "application/json",
            data: JSON.stringify(mailRequestDto),
            success: function (code) {
                if (code) {
                    alert("입력하신 메일로 인증번호가 전송되었습니다.");
                    $("#emailAuth-section").show();
                    startServerBasedCountdown(email);
                    $("#emailAuth").prop("disabled", false).val("");
                    $("#emailAuth-button").prop("disabled", false);
                    $("#email").prop("readonly", true);
                    $("#email-button").prop("disabled", true);
                    $("#change-email-button").show();
                } else {
                    alert("인증번호를 받을 수 없습니다.");
                }
            },
            error: function(xhr) {
                if (xhr.status === 409) {
                    alert(xhr.responseText);
                }
            }
        });
    });

    $("#change-email-button").click(function () {
        clearInterval(emailTimerInterval);
        $("#email-timer").text("");
        $("#emailAuth-section").hide();
        $("#email").prop("readonly", false);
        $("#email-button").prop("disabled", false);
        $("#emailAuth").val("").prop("disabled", true);
        $("#emailAuth-button").prop("disabled", true);
        $("#hiddenEmail").val("");
        $("#change-email-button").hide();
    });

    $("#emailAuth-button").click(function () {
        var email = $("#email").val();
        var authNum = $("#emailAuth").val();

        if (authNum.trim() === "") {
            alert("인증번호를 입력해주세요.");
            return;
        }

        var emailCheckDto = {
            email: email,
            authNum: authNum
        };
        $.ajax({
            type: "POST",
            url: "/signup/emailAuth",
            contentType: "application/json",
            data: JSON.stringify(emailCheckDto),
            success: function (message) {
                if (message) {
                    alert("이메일 인증에 성공하였습니다.");
                    clearInterval(emailTimerInterval);
                    $("#email-timer").text("인증 완료");
                    $("#emailAuth").prop("disabled", true);
                    $("#emailAuth-button").prop("disabled", true);
                    $("#email-button").prop("disabled", true);
                    $("#hiddenEmail").val(email.trim().replace(/,/g, ''));
                    $("#email").prop("readonly", true);
                } else {
                    alert("인증에 실패하였습니다.");
                    $("#emailAuth").val("");
                }
            },
            error: function (xhr) {
                if (xhr.status == 400) {
                    alert("인증번호가 일치하지 않습니다.");
                    $("#emailAuth").val("");
                } else if (xhr.status == 410) {
                    alert("인증 시간이 초과되었습니다. 다시 인증번호를 받아주세요.");
                    $("#emailAuth").prop("disabled", true);
                    $("#emailAuth-button").prop("disabled", true);
                } else if (xhr.status == 500) {
                    alert("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
                } else {
                    alert("알 수 없는 오류가 발생했습니다. (" + xhr.status + ")");
                }
            }
        });
    });

    $("#signup-form").on("submit", function (e) {
        e.preventDefault();

        const fullPhone = getFullPhoneNumber();
        $("#phone").val(fullPhone);

        const form = $(this);
        const formData = form.serialize();
        $.ajax({
            type: "POST",
            url: "/signup",
            data: formData,
            success: function () {
                alert("회원가입이 완료되었습니다!");
                window.location.href = "/login";
            },
            error: function (xhr) {
                const msg = xhr.responseText;
                if (msg === "비밀번호가 일치하지 않습니다.") {
                    alert(msg);
                    $("#password1").val("");
                    $("#password2").val("");
                    return;
                }
                alert(msg);
            }
        });
    });
});

function getFullPhoneNumber() {
    const phone1 = document.getElementById("phone1").value.trim();
    const phone2 = document.getElementById("phone2").value.trim();
    const phone3 = document.getElementById("phone3").value.trim();
    return phone1 + phone2 + phone3;
}

async function sendSMS() {
    const phone1 = document.getElementById("phone1").value.trim();
    const phone2 = document.getElementById("phone2").value.trim();
    const phone3 = document.getElementById("phone3").value.trim();

    if (phone1.length !== 3 || phone2.length !== 4 || phone3.length !== 4) {
        alert("올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)");
        return;
    }

    const phone = phone1 + phone2 + phone3;
    const data = { phone: phone };

    fetch("http://localhost:8080/signup/sms", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            alert('인증번호를 보냈습니다!');
            document.getElementById("phoneAuth-section").style.display = "block";
        })
        .catch(error => {
            console.error('Error:', error);
            alert('오류입니다!');
        });
}

async function verifySMS() {
    const phone = getFullPhoneNumber();
    const certificationNumber = document.getElementById('phoneAuth').value;

    const data = {
        phone: phone,
        certificationNumber: certificationNumber
    };

    fetch('http://localhost:8080/signup/smsAuth', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            alert('인증되었습니다!');
            document.getElementById('hiddenPhone').value = phone.trim().replace(/,/g, '');
            document.getElementById('phoneAuth-button').disabled = true;
            document.getElementById('phone1').readOnly = true;
            document.getElementById('phone2').readOnly = true;
            document.getElementById('phone3').readOnly = true;
            document.getElementById('phoneAuth').disabled = true;
        })
        .catch(error => {
            console.error('Error:', error);
            alert('인증되지 않았습니다!');
        });
}
