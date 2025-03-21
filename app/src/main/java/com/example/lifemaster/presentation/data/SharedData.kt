package com.example.lifemaster.presentation.data

import com.example.lifemaster.presentation.home.pomodoro.PomodoroStatus

object SharedData {
    val todoItems = mutableListOf<String>()
    val pomodoroEmergecyEscapeList = listOf(
//        "계절이 지나가는 하늘에는 가을로 가득 차 있습니다.",
//        "나는 아무 걱정도 없이 별들을 다 헤일 듯합니다.",
//        "가슴 속에 하나 둘 새겨지는 별을 이제 다 못 헤는 것은,",
//        "쉬이 아침이 오는 까닭이요, 내일 밤이 남은 까닭이요,",
//        "아직 나의 청춘이 다하지 않은 까닭입니다.",
//        "별 하나에 추억과 별 하나에 사랑과,",
//        "별 하나에 쓸쓸함과 별 하나에 동경과,",
//        "별 하나에 시와 별 하나에 어머니, 어머니,",
//        "어머님, 나는 별 하나에 아름다운 말 한 마디씩 불러봅니다.",
//        "소학교 때 책상을 같이 했던 아이들의 이름과,",
//        "패, 경, 옥 이런 이국 소녀들의 이름과,",
//        "벌써 아기 어머니 된 계집애들의 이름과,",
//        "가난한 이웃 사람들의 이름과, 비둘기, 강아지, 토끼, 노새, 노루,",
//        "'프랑시스 잠', '라이너 마리아 릴케' 이런 시인의 이름을 불러봅니다.",
//        "이네들은 너무나 멀리 있습니다."
        "계절이 지나가는 하늘에는 가을로 가득 차 있습니다.", "나", "다", "라", "마", "바", "사", "아", "자", "차", "카", "타", "파", "하", "가나" // 테스트용
    )
    var pomodoroStatus: PomodoroStatus = PomodoroStatus.INITIAL
}