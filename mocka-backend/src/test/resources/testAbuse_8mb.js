const abusiveArray = []
const count = 1024 * 1024
for (let i = 0; i < count; i++) {
  abusiveArray.push(Math.random())
}
