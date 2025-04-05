Kotlin에서는 JpaRepository의 반환 타입을 Optional<?> 대신 null로 설정할 수 있다.

---

URL의 상태
- 유효한 URL
- 만료된 URL
- Disabled된 URL
- 삭제된 URL

참고
- 만료 시간은 변경할 수 없음.
- 민료되기 전까지 URL을 disable/enable할 수 있음.
- 만료된 URL은 disable 여부에 관계 없이 삭제와 동일한 취급.
- 삭제된 URL은 복구할 수 없음.
