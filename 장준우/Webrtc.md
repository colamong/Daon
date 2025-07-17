# 3일차

웹 개발을 하다 보면 실시간 화상 통화나 음성 채팅 기능을 구현해야 하는 순간이 찾아옵니다. 기존에 REST API로 JSON 데이터만 주고받던 개발자에게 실시간 스트리밍은 완전히 새로운 영역입니다. WebRTC는 이러한 복잡한 실시간 통신을 웹 브라우저에서 간단하게 구현할 수 있게 해주는 기술입니다.

## [**실시간 통신이 어려운 이유**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#%EC%8B%A4%EC%8B%9C%EA%B0%84%20%ED%86%B5%EC%8B%A0%EC%9D%B4%20%EC%96%B4%EB%A0%A4%EC%9A%B4%20%EC%9D%B4%EC%9C%A0-1)

### [**웹의 태생적 한계**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#%EC%9B%B9%EC%9D%98%20%ED%83%9C%EC%83%9D%EC%A0%81%20%ED%95%9C%EA%B3%84-1-1)

웹은 원래 문서를 공유하기 위해 설계되었습니다. HTTP는 요청하면 응답하는 단순한 구조로, 지속적인 연결이나 실시간 데이터 교환에는 적합하지 않았습니다. 실시간 화상 통화를 구현하려면 근본적으로 다른 접근이 필요했습니다.

전통적인 방식으로 실시간 통신을 구현한다면 고려해야 할 것들이 너무 많습니다. 네트워크 프로토콜 선택부터 시작해서 미디어 코덱 처리, 패킷 손실 대응, 보안 암호화, 그리고 브라우저별 호환성까지 수많은 복잡한 문제들이 기다리고 있습니다.

### [**플러그인 시대의 한계**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#%ED%94%8C%EB%9F%AC%EA%B7%B8%EC%9D%B8%20%EC%8B%9C%EB%8C%80%EC%9D%98%20%ED%95%9C%EA%B3%84-1-2)

과거에는 Flash나 Silverlight 같은 플러그인으로 이 문제를 해결했지만, 보안 취약점과 성능 문제로 인해 결국 웹에서 퇴출되었습니다. 순수 웹 기술만으로 실시간 통신을 구현할 수 있는 새로운 해답이 필요했고, 바로 그 해답이 WebRTC입니다.

## [**WebRTC의 해결책: P2P 직접 연결**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#WebRTC%EC%9D%98%20%ED%95%B4%EA%B2%B0%EC%B1%85%3A%20P2P%20%EC%A7%81%EC%A0%91%20%EC%97%B0%EA%B2%B0-1-3)

### [**중앙 서버의 부담 제거**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#%EC%A4%91%EC%95%99%20%EC%84%9C%EB%B2%84%EC%9D%98%20%EB%B6%80%EB%8B%B4%20%EC%A0%9C%EA%B1%B0-1-4)

WebRTC가 제시한 핵심 아이디어는 단순하지만 혁신적입니다. **서버를 거치지 않고 사용자끼리 직접 연결하자**는 것입니다. 이는 여러 가지 근본적인 이점을 제공합니다.

지연 시간이 최소화되며 서버 부하도 크게 줄어들어 서비스 확장성이 좋아지고, 미디어 데이터가 서버를 거치지 않아 프라이버시도 강화됩니다.

### [**브라우저 네이티브 지원**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#%EB%B8%8C%EB%9D%BC%EC%9A%B0%EC%A0%80%20%EB%84%A4%EC%9D%B4%ED%8B%B0%EB%B8%8C%20%EC%A7%80%EC%9B%90-1-5)

WebRTC는 W3C와 IETF 표준으로 채택되어 모든 주요 브라우저에서 네이티브 지원됩니다. 별도 플러그인 없이 JavaScript API만으로 카메라 접근부터 P2P 전송까지 모든 것이 가능합니다. 이는 웹 플랫폼에서 네이티브 앱 수준의 실시간 통신을 실현한 기술적 성취입니다.

## [**NAT라는 장벽**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#NAT%EB%9D%BC%EB%8A%94%20%EC%9E%A5%EB%B2%BD-1-6)

### [**P2P 연결의 문제**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#P2P%20%EC%97%B0%EA%B2%B0%EC%9D%98%20%EB%AC%B8%EC%A0%9C-1-7)

이론적으로는 완벽한 P2P 연결이지만, 현실은 그렇게 단순하지 않습니다. 가장 큰 장벽은 바로 **NAT(Network Address Translation)**입니다.

대부분의 사용자는 공유기 뒤에 숨어있습니다. 이는 IPv4 주소 부족 문제를 해결하기 위한 불가피한 선택이었지만, P2P 연결에는 큰 걸림돌이 됩니다. 내부에서는 192.168.1.100 같은 사설 IP만 알 수 있고, 상대방이 어떤 공인 IP와 포트로 접근해야 하는지 알기 어렵습니다.

### [**NAT의 다양한 유형**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#NAT%EC%9D%98%20%EB%8B%A4%EC%96%91%ED%95%9C%20%EC%9C%A0%ED%98%95-1-8)

NAT에도 여러 종류가 있고, 각각 다른 특성을 보입니다. **Full Cone NAT**는 비교적 관대해서 한 번 매핑이 생성되면 외부에서 자유롭게 접근할 수 있지만, **Symmetric NAT**는 매우 까다로워서 통신 대상마다 다른 매핑을 생성합니다.

기업 환경에서는 방화벽이라는 추가 장벽도 있습니다. 특정 포트만 허용하고, 트래픽을 면밀히 검사하는 Deep Packet Inspection까지 동원됩니다. 이런 환경에서 P2P 연결을 수립하는 것은 마치 미로에서 출구를 찾는 것과 같습니다.

## [**NAT를 극복하는 방법**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#NAT%EB%A5%BC%20%EA%B7%B9%EB%B3%B5%ED%95%98%EB%8A%94%20%EB%B0%A9%EB%B2%95-1-9)

### [**Signaling**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#Signaling-1-10)

P2P 연결을 위해서는 먼저 상대방을 찾아야 합니다. 마치 전화를 걸기 위해 전화번호를 알아야 하는 것처럼 말입니다. 이 역할을 하는 것이 **Signaling** 과정입니다.

Signaling은 고정 IP를 가진 중개 서버를 통해 이루어집니다. 양쪽 사용자가 이 서버에 접속해서 서로의 연결 정보를 교환합니다. WebRTC 표준에서는 Signaling 방법을 명시하지 않았기 때문에 WebSocket, Socket.IO, 심지어 이메일로도 가능합니다.

중요한 점은 Signaling 서버는 연결 정보만 중개할 뿐, 실제 미디어 데이터는 전혀 거치지 않는다는 것입니다. 마치 소개팅에서 중매인이 처음 만남만 주선하고 나중에는 빠지는 것과 같습니다.

### [**STUN**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#STUN-1-11)

NAT 뒤에 있는 사용자가 자신의 외부 주소를 알아내는 방법이 바로 **STUN(Session Traversal Utilities for NAT)** 서버입니다. 마치 거울을 보고 자신의 모습을 확인하는 것처럼, STUN 서버는 "당신이 외부에서 보이는 주소는 203.142.167.89:62000입니다"라고 알려줍니다.

STUN 서버는 비교적 단순하고 가벼워서 Google같은 회사에서 무료로 제공하기도 합니다. 하지만 모든 NAT 환경에서 동작하는 것은 아닙니다. 특히 Symmetric NAT 환경에서는 STUN만으로는 해결되지 않습니다.

### [**TURN**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#TURN-1-12)

STUN으로도 연결이 안 될 때 등장하는 것이 **TURN(Traversal Using Relays around NAT)** 서버입니다.

TURN 서버는 클라이언트를 대신해서 트래픽을 중계하므로 높은 대역폭과 처리 능력이 필요합니다. 그래서 대부분 유료 서비스이고, 운영 비용도 상당합니다. 하지만 가장 까다로운 네트워크 환경에서도 연결을 보장할 수 있는 역할을 합니다.

## [**실제 연결이 이루어지는 과정**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#%EC%8B%A4%EC%A0%9C%20%EC%97%B0%EA%B2%B0%EC%9D%B4%20%EC%9D%B4%EB%A3%A8%EC%96%B4%EC%A7%80%EB%8A%94%20%EA%B3%BC%EC%A0%95-1-13)

### [**SDP**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#SDP-1-14)

연결할 수 있는 주소를 알았다고 해서 바로 통화가 가능한 것은 아닙니다. 서로 어떤 방식으로 통신할지 합의해야 합니다. 이때 사용하는 것이 **SDP(Session Description Protocol)**입니다.

SDP는 마치 이력서와 같습니다. "저는 H.264와 VP8 코덱을 지원하고, 최대 1080p 해상도까지 가능하며, Opus 오디오 코덱을 사용합니다"라는 식으로 자신의 능력을 상세히 기술합니다.

연결을 요청하는 쪽(Caller)이 먼저 자신의 SDP를 **Offer**로 제시하고, 응답하는 쪽(Callee)이 이를 검토해서 자신이 지원하는 부분만 골라 **Answer**로 응답합니다. 이 과정을 통해 양쪽이 모두 지원하는 최적의 통신 방식이 결정됩니다.

### [**ICE**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#ICE-1-15)

**ICE(Interactive Connectivity Establishment)**는 가능한 모든 연결 경로를 탐색하고 그 중 가장 좋은 것을 선택하는 과정입니다.

ICE는 세 가지 유형의 후보를 수집합니다. **Host Candidate**는 로컬 네트워크의 직접 주소이고, **Server Reflexive Candidate**는 STUN을 통해 얻은 외부 주소이며, **Relay Candidate**는 TURN 서버 주소입니다.

각 후보는 우선순위가 있어서 일반적으로 Host > Server Reflexive > Relay 순서로 시도됩니다. 가장 빠르고 효율적인 경로를 우선 시도하고, 안 되면 차선책을 사용하는 방식입니다.

### [**Connectivity Check**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#Connectivity%20Check-1-16)

ICE는 수집된 후보(로컬 및 원격)를 조합해 여러 가능한 연결 경로를 구성하고, 이 경로들 중 실제로 사용할 수 있는지를 테스트합니다. 이 과정을 **Connectivity Check**라고 합니다.

예를 들어, 로컬 후보가 3개, 원격 후보가 3개라면 총 9개의 조합이 만들어질 수 있습니다. ICE는 이 후보 쌍들을 우선순위에 따라 정렬하고, 각 조합에 대해 실제로 패킷을 전송해 응답이 오는지를 확인합니다.

이 테스트는 후보가 생성되는 즉시 시작되며(Trickle ICE), 가장 먼저 성공적으로 연결된 경로가 **최종 경로**로 선택됩니다.

## [**보안과 품질 관리**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#%EB%B3%B4%EC%95%88%EA%B3%BC%20%ED%92%88%EC%A7%88%20%EA%B4%80%EB%A6%AC-1-17)

### [**자동 암호화로 안전한 통신**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#%EC%9E%90%EB%8F%99%20%EC%95%94%ED%98%B8%ED%99%94%EB%A1%9C%20%EC%95%88%EC%A0%84%ED%95%9C%20%ED%86%B5%EC%8B%A0-1-18)

WebRTC는 모든 통신을 자동으로 암호화합니다. **DTLS(Datagram Transport Layer Security)**로 시그널링 단계에서 키를 교환하고, **SRTP(Secure Real-time Transport Protocol)**로 실제 미디어 스트림을 암호화합니다.

이는 사용자가 별도로 설정할 필요 없이 자동으로 이루어집니다. 마치 HTTPS가 웹 브라우징을 자동으로 암호화하는 것처럼, WebRTC도 실시간 통신을 기본적으로 보호합니다.

### [**네트워크 상황에 맞춘 품질 조절**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#%EB%84%A4%ED%8A%B8%EC%9B%8C%ED%81%AC%20%EC%83%81%ED%99%A9%EC%97%90%20%EB%A7%9E%EC%B6%98%20%ED%92%88%EC%A7%88%20%EC%A1%B0%EC%A0%88-1-19)

WebRTC는 네트워크 상황을 실시간으로 모니터링하면서 품질을 자동 조절합니다. 패킷 손실이 늘어나면 비트레이트를 낮추고, 네트워크가 좋아지면 다시 품질을 높입니다.

이 과정에서 **Google Congestion Control**이라는 정교한 알고리즘이 동작합니다. 단순히 패킷 손실만 보는 것이 아니라 지연 시간, 지터, 대역폭 등을 종합적으로 분석해서 최적의 전송률을 계산합니다.

## [**실제 WebRTC API 활용하기**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#%EC%8B%A4%EC%A0%9C%20WebRTC%20API%20%ED%99%9C%EC%9A%A9%ED%95%98%EA%B8%B0-1-20)

### [**getUserMedia로 미디어 접근**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#getUserMedia%EB%A1%9C%20%EB%AF%B8%EB%94%94%EC%96%B4%20%EC%A0%91%EA%B7%BC-1-21)

WebRTC의 첫 번째 단계는 사용자의 카메라와 마이크에 접근하는 것입니다. 이를 위해 getUserMedia API를 사용합니다.

```jsx
navigator.mediaDevices.getUserMedia({
    video: true,
    audio: true
}).then(stream => {
// 비디오 요소에 스트림 연결document.getElementById('localVideo').srcObject = stream;
}).catch(error => {
    console.error('미디어 접근 실패:', error);
});
```

### [**RTCPeerConnection으로 연결 수립**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#RTCPeerConnection%EC%9C%BC%EB%A1%9C%20%EC%97%B0%EA%B2%B0%20%EC%88%98%EB%A6%BD-1-22)

실제 P2P 연결은 RTCPeerConnection 객체를 통해 이루어집니다. 이 객체가 앞서 설명한 모든 복잡한 과정을 자동화해줍니다.

```jsx
const pc = new RTCPeerConnection({
    iceServers: [
        { urls: 'stun:stun.l.google.com:19302' }
    ]
});
```

### [**순수 WebSocket으로 Signaling 구현**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#%EC%88%9C%EC%88%98%20WebSocket%EC%9C%BC%EB%A1%9C%20Signaling%20%EA%B5%AC%ED%98%84-1-23)

Spring Boot는 단순한 WebSocket 중개자 역할을 담당합니다. 복잡한 STOMP나 SockJS 없이도 충분히 동작합니다.

### [**프론트엔드 (WebSocket 연결)**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#%ED%94%84%EB%A1%A0%ED%8A%B8%EC%97%94%EB%93%9C%20(WebSocket%20%EC%97%B0%EA%B2%B0)-1-24)

```jsx
// WebSocket 연결 설정 (Spring Boot 서버와 연결)const socket = new WebSocket('ws://localhost:8080/signaling');

// 메시지 수신 처리
socket.onmessage = async (event) => {
    const message = JSON.parse(event.data);

    switch (message.type) {
        case 'offer':
            await handleOffer(message.offer);
            break;
        case 'answer':
            await handleAnswer(message.answer);
            break;
        case 'ice-candidate':
            await handleIceCandidate(message.candidate);
            break;
    }
};
```

### [**Offer/Answer 교환 구현**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#Offer%2FAnswer%20%EA%B5%90%ED%99%98%20%EA%B5%AC%ED%98%84-1-25)

실제 연결 과정에서는 SDP Offer와 Answer를 교환해야 합니다. 여기서 Spring Boot가 단순한 중개자 역할을 담당합니다.

### [**Caller 측 Offer 생성**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#Caller%20%EC%B8%A1%20Offer%20%EC%83%9D%EC%84%B1-1-26)

```jsx
// 통화를 시작하는 측에서 Offer 생성async function startCall() {
    const offer = await pc.createOffer();
    await pc.setLocalDescription(offer);

// Spring Boot 서버를 통해 상대방에게 전송
    socket.send(JSON.stringify({
        type: 'offer',
        offer: offer
    }));
}

```

### [**Callee 측 Answer 생성**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#Callee%20%EC%B8%A1%20Answer%20%EC%83%9D%EC%84%B1-1-27)

```jsx
// Offer를 받은 측에서 Answer 생성async function handleOffer(offer) {
    await pc.setRemoteDescription(offer);

    const answer = await pc.createAnswer();
    await pc.setLocalDescription(answer);

// Spring Boot 서버를 통해 응답 전송
    socket.send(JSON.stringify({
        type: 'answer',
        answer: answer
    }));
}
```

### [**Spring Boot의 간단한 중개 역할**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#Spring%20Boot%EC%9D%98%20%EA%B0%84%EB%8B%A8%ED%95%9C%20%EC%A4%91%EA%B0%9C%20%EC%97%AD%ED%95%A0-1-28)

Spring Boot는 받은 메시지를 단순히 다른 클라이언트들에게 전달하는 역할만 수행합니다.

### [**WebSocket 설정**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#WebSocket%20%EC%84%A4%EC%A0%95-1-29)

```less
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new SignalingHandler(), "/signaling")
                .setAllowedOrigins("*");
    }
}

```

### [**메시지 중개 Handler**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#%EB%A9%94%EC%8B%9C%EC%A7%80%20%EC%A4%91%EA%B0%9C%20Handler-1-30)

```java
@Component
public class SignalingHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
// 발신자를 제외한 모든 클라이언트에게 메시지 전달for (WebSocketSession s : sessions) {
            if (!s.equals(session) && s.isOpen()) {
                s.sendMessage(message);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }
}

```

### [**ICE Candidate 교환**](https://leve68.tistory.com/entry/WebRTC%EB%A1%9C-%EC%8B%9C%EC%9E%91%ED%95%98%EB%8A%94-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%9B%B9-%ED%86%B5%EC%8B%A0#ICE%20Candidate%20%EA%B5%90%ED%99%98-1-31)

네트워크 연결 경로를 찾기 위해 ICE Candidate 정보도 교환해야 합니다.

```jsx
// ICE Candidate 이벤트 처리 (발견한 클라이언트에서 발신)
pc.onicecandidate = (event) => {
    if (event.candidate) {
        socket.send(JSON.stringify({
            type: 'ice-candidate',
            candidate: event.candidate
        }));
    }
};

// ICE Candidate 수신 처리 (응답이 요구되지 않음)async function handleIceCandidate(candidate) {
    await pc.addIceCandidate(candidate);
}
```