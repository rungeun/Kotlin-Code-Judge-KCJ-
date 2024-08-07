package com.github.rungeun.kcj.kotlincodejudge

import com.intellij.ui.JBColor
import javax.swing.*
import java.awt.*

class MyToolWindowUI {
    val content: JPanel = JPanel()

    private val outBackground: JBColor = JBColor.GREEN
    private val inBackground: JBColor = JBColor.WHITE

    init {
        // Y1 최상위 레이아웃
        content.layout = BoxLayout(content, BoxLayout.Y_AXIS)
        content.background = outBackground

        // Y2 기능 버튼 영역
        val y2Panel = JPanel()
        y2Panel.layout = BoxLayout(y2Panel, BoxLayout.Y_AXIS)
        y2Panel.background = inBackground
        y2Panel.isOpaque = true
        y2Panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10) // 패널 내부 패딩 추가

        // Y2 X1 기능 버튼/1행
        val y2x1Panel = JPanel()
        y2x1Panel.layout = BoxLayout(y2x1Panel, BoxLayout.X_AXIS)
        y2x1Panel.isOpaque = false
        val y1x1bt1Run = JButton("Run")
        val y1x1bt2SomeRun = JButton("Some Run")
        val y1x1bt3Stop = JButton("Stop")

        y2x1Panel.add(y1x1bt1Run)
        y2x1Panel.add(y1x1bt2SomeRun)
        y2x1Panel.add(y1x1bt3Stop)

        // Y2 X2 기능 버튼/2행
        val y2x2Panel = JPanel()
        y2x2Panel.layout = BoxLayout(y2x2Panel, BoxLayout.X_AXIS)
        y2x2Panel.isOpaque = false
        val y2x2bt1Donate = JButton("Give coffee :>")
        val y2x2bt2Guide = JButton("Guide")

        y2x2Panel.add(y2x2bt1Donate)
        y2x2Panel.add(y2x2bt2Guide)

        y2Panel.add(y2x1Panel)
        y2Panel.add(y2x2Panel)

        // Y3 UTC
        val y3Panel = JPanel()
        y3Panel.layout = BoxLayout(y3Panel, BoxLayout.Y_AXIS)
        y3Panel.background = inBackground
        y3Panel.isOpaque = true
        y3Panel.border = BorderFactory.createEmptyBorder( 10, 10, 10, 10) // 패널 내부 패딩 추가

        // Y3 X1  UTC/테스트케이스 넘버, ADD 버튼, DEL 버튼
        val y3x1Panel = JPanel()
        y3x1Panel.layout = BoxLayout(y3x1Panel, BoxLayout.X_AXIS)
        y3x1Panel.isOpaque = false
        val y3x1Label = JLabel("UTC 1") // TODO: 번호를 변수로 바꿔줘야 함
        val y3x1bt1Add = JButton("Copy TestCase")
        val y3x1bt2Del = JButton("Delete")

        y3x1Panel.add(y3x1Label)
        y3x1Panel.add(Box.createHorizontalGlue()) // 공백 추가
        y3x1Panel.add(y3x1bt1Add)
        y3x1Panel.add(y3x1bt2Del)

        // Y3 X2 UTC/IN 라벨, COPY 버튼 /////////////////////////////////////////////////////////////////////////////////
        val y3x2Panel = JPanel()
        y3x2Panel.layout = BoxLayout(y3x2Panel, BoxLayout.X_AXIS)
        val y3x2inLabelIn = JLabel("In")
        val y3x2bt1Copy = JButton("Copy")

        y3x2Panel.add(y3x2inLabelIn)
        y3x2Panel.add(Box.createHorizontalGlue())
        y3x2Panel.add(y3x2bt1Copy) // 버튼 추가
        // Y3 X3 입력창
        val y3x3Panel = JPanel()
        y3x3Panel.layout = BoxLayout(y3x3Panel, BoxLayout.X_AXIS)
        y3x3Panel.isOpaque = false

        // JTextArea 사용
        val y3x3TextAreaIn = JTextArea(4, 5) // 4줄 높이, 20글자 너비의 텍스트 영역
        y3x3TextAreaIn.lineWrap = true // 자동 줄바꿈 설정
        y3x3TextAreaIn.wrapStyleWord = true // 단어 단위로 줄바꿈

        // 스크롤 패널에 텍스트 영역 추가
        val y3x3scrollPaneIn = JScrollPane(y3x3TextAreaIn)
        y3x3scrollPaneIn.preferredSize = Dimension(200, 100) // 스크롤 패널의 선호 크기 설정
        y3x3scrollPaneIn.maximumSize = Dimension(Int.MAX_VALUE, 100) // 스크롤 패널의 최대 크기 설정, 높이는 고정

        y3x3Panel.add(y3x3scrollPaneIn)

        // COPY 버튼에 액션 리스너 추가
        y3x2bt1Copy.addActionListener(CopyActionListener(y3x2bt1Copy) { y3x3TextAreaIn.text })

        // Y3 X4 UTC/OUT 라벨, COPY 버튼 /////////////////////////////////////////////////////////////////////////////////
        val y3x4Panel = JPanel()
        y3x4Panel.layout = BoxLayout(y3x4Panel, BoxLayout.X_AXIS)
        val y3x4inLabelIn = JLabel("Out")
        val y3x4bt1Copy = JButton("Copy")

        y3x4Panel.add(y3x4inLabelIn)
        y3x4Panel.add(Box.createHorizontalGlue())
        y3x4Panel.add(y3x4bt1Copy) // 버튼 추가
        // Y3 X5 UTC/입력창
        val y3x5Panel = JPanel()
        y3x5Panel.layout = BoxLayout(y3x5Panel, BoxLayout.X_AXIS)
        y3x5Panel.isOpaque = false

        // JTextArea 사용
        val y3x5TextAreaIn = JTextArea(3, 5) // 4줄 높이, 20글자 너비의 텍스트 영역
        y3x5TextAreaIn.lineWrap = true // 자동 줄바꿈 설정
        y3x5TextAreaIn.wrapStyleWord = true // 단어 단위로 줄바꿈

        // 스크롤 패널에 텍스트 영역 추가
        val y3x5scrollPaneIn = JScrollPane(y3x5TextAreaIn)
        y3x5scrollPaneIn.preferredSize = Dimension(200, 100) // 스크롤 패널의 선호 크기 설정
        y3x5scrollPaneIn.maximumSize = Dimension(Int.MAX_VALUE, 100) // 스크롤 패널의 최대 크기 설정, 높이는 고정

        y3x5Panel.add(y3x5scrollPaneIn)

        // COPY 버튼에 액션 리스너 추가
        y3x4bt1Copy.addActionListener(CopyActionListener(y3x4bt1Copy) { y3x5TextAreaIn.text })

        // Y3 X6 UTC/Answer 라벨, COPY 버튼 /////////////////////////////////////////////////////////////////////////////////
        val y3x6Panel = JPanel()
        y3x6Panel.layout = BoxLayout(y3x6Panel, BoxLayout.X_AXIS)
        val y3x6inLabelOut = JLabel("Answer")
        val y3x6bt1Copy = JButton("Copy")

        y3x6Panel.add(y3x6inLabelOut)
        y3x6Panel.add(Box.createHorizontalGlue())
        y3x6Panel.add(y3x6bt1Copy) // 버튼 추가

        // Y3 X7 UTC/출력창
        val y3x7Panel = JPanel()
        y3x7Panel.layout = BoxLayout(y3x7Panel, BoxLayout.X_AXIS)
        y3x7Panel.isOpaque = false

        // JTextArea 사용
        val y3x7TextAreaOut = JTextArea(4, 5) // 4줄 높이, 20글자 너비의 텍스트 영역
        y3x7TextAreaOut.isEditable = false // 편집 불가능하게 설정
        y3x7TextAreaOut.lineWrap = true // 자동 줄바꿈 설정
        y3x7TextAreaOut.wrapStyleWord = true // 단어 단위로 줄바꿈
        y3x7TextAreaOut.text = "TEST1" // 출력 내용 설정
        y3x7TextAreaOut.dragEnabled = true // 드래그 가능하게 설정

        // 스크롤 패널에 텍스트 영역 추가
        val y3x7scrollPaneOut = JScrollPane(y3x7TextAreaOut)
        y3x7scrollPaneOut.preferredSize = Dimension(200, 100) // 스크롤 패널의 선호 크기 설정
        y3x7scrollPaneOut.maximumSize = Dimension(Int.MAX_VALUE, 100) // 스크롤 패널의 최대 크기 설정, 높이는 고정

        y3x7Panel.add(y3x7scrollPaneOut)
        // COPY 버튼에 액션 리스너 추가
        y3x6bt1Copy.addActionListener(CopyActionListener(y3x6bt1Copy) { y3x7TextAreaOut.text })

        // Y3 X8 UTC/Cerr 라벨, COPY 버튼 /////////////////////////////////////////////////////////////////////////////////
        val y3x8Panel = JPanel()
        y3x8Panel.layout = BoxLayout(y3x8Panel, BoxLayout.X_AXIS)
        val y3x8inLabelOut = JLabel("Cerr")
        val y3x8bt1Copy = JButton("Copy")

        y3x8Panel.add(y3x8inLabelOut)
        y3x8Panel.add(Box.createHorizontalGlue())
        y3x8Panel.add(y3x8bt1Copy) // 버튼 추가

        // Y3 X9  UTC/출력창
        val y3x9Panel = JPanel()
        y3x9Panel.layout = BoxLayout(y3x9Panel, BoxLayout.X_AXIS)
        y3x9Panel.isOpaque = false

        // JTextArea 사용
        val y3x9TextAreaOut = JTextArea(4, 5) // 4줄 높이, 20글자 너비의 텍스트 영역
        y3x9TextAreaOut.isEditable = false // 편집 불가능하게 설정
        y3x9TextAreaOut.lineWrap = true // 자동 줄바꿈 설정
        y3x9TextAreaOut.wrapStyleWord = true // 단어 단위로 줄바꿈
        y3x9TextAreaOut.text = "TEST2" // 출력 내용 설정
        y3x9TextAreaOut.dragEnabled = true // 드래그 가능하게 설정

        // 스크롤 패널에 텍스트 영역 추가
        val y3x9scrollPaneOut = JScrollPane(y3x9TextAreaOut)
        y3x9scrollPaneOut.preferredSize = Dimension(200, 100) // 스크롤 패널의 선호 크기 설정
        y3x9scrollPaneOut.maximumSize = Dimension(Int.MAX_VALUE, 100) // 스크롤 패널의 최대 크기 설정, 높이는 고정

        y3x9Panel.add(y3x9scrollPaneOut)

        // COPY 버튼에 액션 리스너 추가
        y3x8bt1Copy.addActionListener(CopyActionListener(y3x8bt1Copy) { y3x9TextAreaOut.text })


        y3Panel.add(y3x1Panel)
        y3Panel.add(y3x2Panel)
        y3Panel.add(y3x3Panel)
        y3Panel.add(y3x4Panel)
        y3Panel.add(y3x5Panel)
        y3Panel.add(y3x6Panel)
        y3Panel.add(y3x7Panel)
        y3Panel.add(y3x8Panel)
        y3Panel.add(y3x9Panel)


        // Y4 (Y3)추가 생성 ADD 버튼
        val y4Panel = JPanel()
        y4Panel.layout = BoxLayout(y4Panel, BoxLayout.Y_AXIS)
        y4Panel.background = inBackground
        y4Panel.isOpaque = true
        y4Panel.border = BorderFactory.createEmptyBorder( 10, 10, 10, 10) // 패널 내부 패딩 추가

        // Y4 X1  UTC/테스트케이스 넘버, ADD 버튼, DEL 버튼
        val y4x1Panel = JPanel()
        y4x1Panel.layout = BoxLayout(y4x1Panel, BoxLayout.X_AXIS)
        y4x1Panel.isOpaque = false
        val y4x1bt1NewAdd = JButton("New TestCase")

        y4x1Panel.add(y4x1bt1NewAdd)

        y4Panel.add(y4x1Panel)
        /*
                // Y3 X?? ADD
                val innerBottomRightPanel = JPanel()
                innerBottomRightPanel.layout = BoxLayout(innerBottomRightPanel, BoxLayout.X_AXIS)
                innerBottomRightPanel.isOpaque = false
                val bt7 = JButton("Button 7")
                val bt8 = JButton("Button 8")
                bt7.preferredSize = Dimension(200, 100)
                bt8.preferredSize = Dimension(200, 100)
                innerBottomRightPanel.add(bt7)
                innerBottomRightPanel.add(bt8)


                y3Panel.add(innerBottomRightPanel)
        */



        // Y_AXIS 패널
        content.add(Box.createVerticalStrut(10)) // 상단 마진 추가
        content.add(y2Panel)
        content.add(Box.createVerticalStrut(10)) // 상단과 하단 패널 사이에 마진 추가
        content.add(y3Panel)
        content.add(y4Panel)
        content.add(Box.createVerticalStrut(10)) // 하단 마진 추가


        content.isVisible=true
    }

}



/* 공백 추가
 y3x1Panel.add(Box.createHorizontalGlue()) // 공백 추가
 */
/* 배경 설정
import javax.swing.*
import java.awt.*

fun main() {
    // 메인 프레임 생성
    val frame = JFrame("Background Example")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.setSize(800, 600)
    frame.layout = BorderLayout()

    // 특정 크기와 배경 색상을 가진 패널 생성
    val fixedSizePanel = JPanel()
    fixedSizePanel.isOpaque = true // 불투명하게 설정하여 배경 색상이 보이도록 함
    fixedSizePanel.background = Color.RED // 배경 색상 설정
    fixedSizePanel.preferredSize = Dimension(300, 200) // 크기 설정

    // 화면 좌우를 꽉 채우는 배경 색상을 가진 패널 생성
    val fullWidthPanel = JPanel()
    fullWidthPanel.isOpaque = true // 불투명하게 설정하여 배경 색상이 보이도록 함
    fullWidthPanel.background = Color.BLUE // 배경 색상 설정
    fullWidthPanel.preferredSize = Dimension(frame.width, 100) // 화면 좌우를 꽉 채우도록 설정
    fullWidthPanel.maximumSize = Dimension(Int.MAX_VALUE, 100) // 최대 크기 설정 (높이는 고정, 너비는 최대)

    // 레이아웃 설정 및 패널 추가
    frame.layout = BoxLayout(frame.contentPane, BoxLayout.Y_AXIS)
    frame.add(fixedSizePanel)
    frame.add(fullWidthPanel)

    // 프레임을 화면에 표시
    frame.isVisible = true
}


 */